package badasintended.slotlink.gui.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.common.registry.NetworkRegistry.REQUEST_CURSOR
import badasintended.slotlink.common.registry.NetworkRegistry.REQUEST_REMOVE
import badasintended.slotlink.common.registry.ScreenHandlerRegistry
import badasintended.slotlink.common.util.*
import badasintended.slotlink.gui.widget.WServerSlot
import badasintended.slotlink.gui.widget.WSyncedSlot
import badasintended.slotlink.inventory.DummyInventory
import badasintended.slotlink.mixin.ScreenHandlerAccessor
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import spinnery.common.utility.StackUtilities
import spinnery.widget.WSlot
import spinnery.widget.api.Action
import spinnery.widget.api.Action.*
import spinnery.widget.api.Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK
import spinnery.widget.api.Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK
import java.util.*
import kotlin.collections.set

open class RequestScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val masterPos: BlockPos,
    val lastSort: SortBy,
    private val context: ScreenHandlerContext
) : ModScreenHandler(syncId, playerInventory), MasterWatcher {

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val inputSlots = arrayListOf<WSlot>()
    private val outputSlot: WSlot

    private val playerSlots = arrayListOf<WSlot>()
    val linkedSlots = arrayListOf<WSlot>()

    private val buffer2: WSlot
    private val buffer3: WSlot

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInventory, buf.readBlockPos(), SortBy.of(buf.readVarInt()), ScreenHandlerContext.EMPTY
    ) {
        buf.readIntArray().forEachIndexed { invN, size ->
            inventories[invN + 3] = DummyInventory(size)
            for (slotN in 0 until size) {
                val slot = root.createChild { WSyncedSlot(this::onSetStack) }
                slot.setNumber<WSlot>(invN + 3, slotN)
                linkedSlots.add(slot)
            }
        }
    }

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        masterPos: BlockPos,
        invMap: Map<Inventory, Pair<Boolean, Set<Item>>>,
        lastSort: SortBy,
        context: ScreenHandlerContext
    ) : this(syncId, playerInventory, masterPos, lastSort, context) {
        var i = 3
        invMap.forEach { (inv, filter) ->
            inventories[i] = inv
            for (j in 0 until inv.size()) {
                val slot = root.createChild { WServerSlot(this::onSetStack) }
                slot.setInventoryNumber<WSlot>(i)
                slot.setSlotNumber<WSlot>(j)
                if (filter.second.isNotEmpty()) {
                    if (filter.first) {
                        slot.setBlacklist<WSlot>()
                        slot.refuse<WSlot>(*filter.second.toTypedArray())
                    } else {
                        slot.setWhitelist<WSlot>()
                        slot.accept<WSlot>(*filter.second.toTypedArray())
                    }
                }
                linkedSlots.add(slot)
                slot.setStack<WSlot>(inv.getStack(j))
            }
            i++
        }
    }

    init {
        inventories[-3] = DummyInventory(1)
        inventories[-2] = DummyInventory(1)
        inventories[-1] = DummyInventory(8 * 6)
        inventories[1] = craftingInv
        inventories[2] = resultInv

        // buffers
        WSlot.addHeadlessArray(root, 0, -1, 8, 6)

        buffer2 = root.createChild { WSlot() }
        buffer2.setInventoryNumber<WSlot>(-2)
        buffer2.setSlotNumber<WSlot>(0)

        buffer3 = root.createChild { WSlot() }
        buffer3.setInventoryNumber<WSlot>(-3)
        buffer3.setSlotNumber<WSlot>(0)

        for (j in 0 until 9) {
            val slot = root.createChild { WServerSlot(this::onSetStack) }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(j)
            inputSlots.add(slot)
        }

        outputSlot = root.createChild { WServerSlot(this::onSetStack) }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        for (j in 0 until 36) {
            val slot = root.createChild { WServerSlot(this::onSetStack) }
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(j)
            playerSlots.add(slot)
        }
    }

    fun validateInventories() {
        context.run { world, _ ->
            if (!world.isClient) {
                val removedInventories = linkedSlots
                    .filter { it.linkedInventory == null }
                    .stream()
                    .mapToInt { it.inventoryNumber }
                    .distinct()
                    .toArray()
                linkedSlots.removeIf { it.linkedInventory == null }
                val buf = buf()
                buf.writeIntArray(removedInventories)
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, REQUEST_REMOVE, buf)
            }
        }
    }

    fun craftOnce() {
        val output = outputSlot.stack
        val cursor = playerInventory.cursorStack
        if (!StackUtilities.equalItemAndTag(output, cursor) and !cursor.isEmpty) return
        if ((output.count + cursor.count) > output.maxCount) return

        validateInventories()

        craftInternal()

        val buffer = buffer3.stack
        StackUtilities
            .merge(buffer, cursor, buffer.maxCount, cursor.maxCount)
            .apply(buffer3::acceptStack, playerInventory::setCursorStack)
    }

    fun craftStack() {
        validateInventories()

        val outputStack = outputSlot.stack
        val craftMax = outputStack.maxCount / outputStack.count.coerceAtLeast(1)

        for (i in 0 until craftMax) craftInternal()

        onSlotAction(0, -3, 0, QUICK_MOVE, player)
    }

    fun clearCraft() {
        onSlotAction(0, -3, 0, PICKUP, player)

        val filledInput = inputSlots.filterNot { it.stack.isEmpty }
        filledInput.forEach { slot ->
            onSlotAction(slot.slotNumber, slot.inventoryNumber, 0, PICKUP, player)
            onSlotAction(0, -2, 0, PICKUP, player)
            onSlotAction(0, -2, 0, QUICK_MOVE, player)
            onSlotAction(slot.slotNumber, slot.inventoryNumber, 0, PICKUP, player)
        }

        onSlotAction(0, -3, 0, PICKUP, player)

        craftItem()
    }

    fun pullInput(outside: ArrayList<ArrayList<Item>>) {
        clearCraft()
        dropInventory(player, world, craftingInv)

        playerSlots.sortByDescending { it.stack.count }
        linkedSlots.sortByDescending { it.stack.count }

        outside.forEachIndexed { slotN, inside ->
            for (item in inside) {
                if (item == Items.AIR) continue
                var first = playerSlots.firstOrNull { it.stack.item == item }
                if (first == null) first = linkedSlots.firstOrNull { it.stack.item == item }
                if (first != null) {
                    val stack = first.stack.copy()
                    stack.count = 1
                    stack.tag = first.stack.tag
                    first.stack.decrement(1)
                    inputSlots[slotN].setStack<WSlot>(stack)
                    break
                }
            }
        }

        craftItem()
    }

    private fun craftInternal() {
        if (!StackUtilities.equalItemAndTag(outputSlot.stack, buffer3.stack) and !buffer3.stack.isEmpty) return

        StackUtilities
            .merge(outputSlot.stack, buffer3.stack, outputSlot.stack.maxCount, buffer3.stack.maxCount)
            .apply(outputSlot::acceptStack, buffer3::acceptStack)

        val remainingStacks = world.recipeManager.getRemainingStacks(RecipeType.CRAFTING, craftingInv, world)

        val filledInput = inputSlots.filterNot { it.stack.isEmpty }
        filledInput.forEach { slot ->
            if (remainingStacks[slot.slotNumber].isEmpty) {
                if (slot.stack.count == 1) {
                    val first = linkedSlots.firstOrNull { StackUtilities.equalItemAndTag(it.stack, slot.stack) }
                    if (first == null) slot.stack.decrement(1) else first.stack.decrement(1)
                } else {
                    slot.stack.decrement(1)
                }
            } else {
                slot.setStack<WSlot>(remainingStacks[slot.slotNumber])
            }
        }

        craftItem()
    }

    /**
     * Taken from [CraftingScreenHandler]
     */
    private fun craftItem() {
        if (!world.isClient) {
            player as ServerPlayerEntity
            var itemStack = ItemStack.EMPTY
            val optional = world.server!!.recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInv, world)
            if (optional.isPresent) {
                val craftingRecipe = optional.get()
                if (resultInv.shouldCraftRecipe(world, player, craftingRecipe)) {
                    itemStack = craftingRecipe.craft(craftingInv)
                }
            }
            outputSlot.setStack<WSlot>(itemStack)
            resultInv.unlockLastRecipe(player)
        }
    }

    private fun onSetStack(invN: Int, slot: Int, stack: ItemStack) {
        if (world.isClient) screen { it.sort() }
    }

    private fun screen(x: (RequestScreen<*>) -> Any) {
        if (!world.isClient) return
        this as ScreenHandlerAccessor
        listeners.filterIsInstance<RequestScreen<*>>().forEach { x.invoke(it) }
    }

    override fun onContentChanged(inventory: Inventory) {
        if ((inventory == craftingInv)) {
            craftItem()
        } else super.onContentChanged(inventory)

        this as ScreenHandlerAccessor
    }

    /**
     * Make [Action.QUICK_MOVE] does not target crafting slots also
     * makes it target player inventory if the slot is one
     * of the [linkedSlots] and vice versa.
     */
    override fun onSlotAction(
        slotNumber: Int, inventoryNumber: Int, button: Int, action: Action, player: PlayerEntity
    ) {
        if (world.isClient) return

        this as ScreenHandlerAccessor

        validateInventories()

        val source: WSlot = root.getSlot(inventoryNumber, slotNumber) ?: return
        if (source.isLocked) return

        val cursorStack = playerInventory.cursorStack

        linkedSlots.sortByDescending { it.stack.count }
        playerSlots.sortBy { it.slotNumber }
        playerSlots.sortByDescending { it.stack.count }

        if (action == QUICK_MOVE) {
            val targets = arrayListOf<WSlot>()
            when (inventoryNumber) {
                // when in player inventory, target container slots first
                0 -> {
                    targets.addAll(linkedSlots)
                    targets.addAll(playerSlots)
                }

                // when in crafting slots, target player inventory first
                1, 2 -> {
                    targets.addAll(playerSlots)
                    targets.addAll(linkedSlots)
                }

                // buffer
                -2 -> targets.addAll(linkedSlots)
                -3 -> targets.addAll(playerSlots)

                // when in container slots, only target player inventory
                else -> targets.addAll(playerSlots)
            }

            for (target in targets) {
                if ((target.inventoryNumber == inventoryNumber) and (target.slotNumber == slotNumber)) continue
                if (target.refuses(source.stack) or target.isLocked) continue

                if ((!source.stack.isEmpty and target.stack.isEmpty) or (StackUtilities.equalItemAndTag(
                        source.stack, target.stack
                    ) and (target.stack.count < target.maxCount))
                ) {
                    val max = if (target.stack.isEmpty) source.maxCount else target.maxCount
                    source.consume(action, FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK)
                    StackUtilities
                        .merge(source::getStack, target::getStack, source::getMaxCount) { max }
                        .apply({ source.setStack<WSlot>(it) }, { target.setStack<WSlot>(it) })
                    if ((source.inventoryNumber in arrayOf(2, 0, -2, -3)) and !source.stack.isEmpty) {
                        continue
                    } else break
                }
            }
            if (inventoryNumber in -3..-2) {
                val buffer = root.getSlot<WSlot>(inventoryNumber, 0)
                if (!buffer.stack.isEmpty) {
                    playerInventory.cursorStack = buffer.stack
                    buffer.setStack<WSlot>(ItemStack.EMPTY)
                }
            }
        } else if (action == PICKUP_ALL) {
            playerSlots.forEach { slot ->
                if (StackUtilities.equalItemAndTag(slot.stack, cursorStack) and !slot.isLocked) {
                    slot.consume(action, FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK)
                    StackUtilities
                        .merge(slot::getStack, { cursorStack }, slot::getMaxCount, { cursorStack.maxCount })
                        .apply({ slot.setStack<WSlot>(it) }, { playerInventory.cursorStack = it })
                }
            }
        } else super.onSlotAction(slotNumber, inventoryNumber, button, action, player)

        val buf = buf()
        buf.writeItemStack(playerInventory.cursorStack)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, REQUEST_CURSOR, buf)
    }

    override fun close(player: PlayerEntity) {
        clearCraft()
        dropInventory(player, world, craftingInv)
        context.run { world, pos ->
            val master = world.getBlockEntity(pos)
            if (master is MasterBlockEntity) master.watchers.remove(this)
        }
        super.close(player)
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.REQUEST
    }

    override fun onMasterRemoved() {
        if (player is ServerPlayerEntity) player.closeHandledScreen()
        screen { it.onClose() }
    }

}
