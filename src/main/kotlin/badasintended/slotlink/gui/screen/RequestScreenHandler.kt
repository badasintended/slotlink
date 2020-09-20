package badasintended.slotlink.gui.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.gui.widget.*
import badasintended.slotlink.inventory.DummyInventory
import badasintended.slotlink.inventory.SyncedInventory
import badasintended.slotlink.mixin.ScreenHandlerAccessor
import badasintended.slotlink.registry.NetworkRegistry.REQUEST_CURSOR
import badasintended.slotlink.registry.NetworkRegistry.REQUEST_INIT_CLIENT
import badasintended.slotlink.registry.NetworkRegistry.REQUEST_REMOVE
import badasintended.slotlink.registry.NetworkRegistry.SYNC_STACKS
import badasintended.slotlink.registry.ScreenHandlerRegistry
import badasintended.slotlink.util.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import sbinnery.common.registry.NetworkRegistry.SLOT_UPDATE_PACKET
import sbinnery.common.registry.NetworkRegistry.createSlotUpdatePacket
import sbinnery.common.utility.StackUtilities
import sbinnery.widget.WInterface
import sbinnery.widget.WSlot
import sbinnery.widget.api.Action
import sbinnery.widget.api.Action.*
import sbinnery.widget.api.Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK
import sbinnery.widget.api.Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK
import java.util.*
import kotlin.collections.set

open class RequestScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val requestPos: BlockPos,
    val lastSort: Sort,
    private val master: MasterBlockEntity?
) : ModScreenHandler(syncId, playerInventory), MasterWatcher {

    private val craftingInv = CraftingInventory(this, 3, 3)
    val resultInv = CraftingResultInventory()

    private val inputSlots = arrayListOf<WSlot>()
    private val outputSlot: WSlot

    private val playerSlots = arrayListOf<WSlot>()
    val linkedSlots = arrayListOf<WSlot>()

    private val buffer2: WSlot
    private val buffer3: WSlot

    private val syncedInventory = SyncedInventory()
    private val syncedInterface = WSyncedInterface(this, this::sort)

    private var initialized = false

    /** Client side **/
    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInventory, buf.readBlockPos(), Sort.of(buf.readVarInt()), null
    )

    /** Server side **/
    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        masterPos: BlockPos,
        invMap: Map<Inventory, Pair<Boolean, Set<Item>>>,
        lastSort: Sort,
        world: World,
        master: MasterBlockEntity
    ) : this(syncId, playerInventory, masterPos, lastSort, master) {
        this.world = world
        val root = `interface`

        var i = 3
        invMap.forEach { (inv, filter) ->
            inventories[i] = inv
            for (j in 0 until inv.size()) {
                val slot = root.createChild { WServerSlot(this::sort) }
                slot.setNumber<WSlot>(i, j)
                if (filter.second.isNotEmpty()) {
                    if (filter.first) {
                        slot.setBlacklist<WSlot>()
                        slot.refuse<WSlot>(*filter.second.toTypedArray())
                    } else {
                        slot.setWhitelist<WSlot>()
                        slot.accept<WSlot>(*filter.second.toTypedArray())
                    }
                }
                slot.setMaximumCount<WSlot>(inv.maxCountPerStack)
                linkedSlots.add(slot)
                val stack = inv.getStack(j)
                slot.setStack<WSlot>(stack)
            }
            i++
        }
    }

    init {
        val root = `interface`

        inventories[-3] = DummyInventory(1)
        inventories[-2] = DummyInventory(1)
        inventories[1] = craftingInv
        inventories[2] = resultInv

        buffer2 = root.createChild { WSlot() }
        buffer2.setInventoryNumber<WSlot>(-2)
        buffer2.setSlotNumber<WSlot>(0)

        buffer3 = root.createChild { WSlot() }
        buffer3.setInventoryNumber<WSlot>(-3)
        buffer3.setSlotNumber<WSlot>(0)

        for (j in 0 until 9) {
            val slot = root.createChild { WServerSlot(this::sort) }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(j)
            inputSlots.add(slot)
        }

        outputSlot = root.createChild { WCraftingResultSlot(this, this::sort) }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        for (j in 0 until 36) {
            val slot = root.createChild { WServerSlot(this::sort) }
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(j)
            playerSlots.add(slot)
        }
    }

    fun init() {
        if (!initialized) {
            initialized = true
            if (world.isClient) {
                sort()
            } else {
                val stacks = linkedSlots
                    .filter { !it.stack.isEmpty }
                    .distinctBy { it.stack.item to it.stack.tag }
                    .map { it.stack.copy().apply { count = 1 } }

                stacks.forEach { stack ->
                    val buf = buf().apply {
                        writeVarInt(syncId)
                        writeItemStack(stack)
                        val slots = linkedSlots.filter { StackUtilities.equalItemAndTag(stack, it.stack) }
                        writeVarInt(slots.size)
                        slots.forEach { writeIntArray(intArrayOf(it.inventoryNumber, it.slotNumber, it.stack.count)) }
                    }
                    s2c(player, SYNC_STACKS, buf)
                }
                s2c(player, REQUEST_INIT_CLIENT, buf().writeVarInt(syncId))
                master?.forceChunk(world)
            }
        }
    }

    fun validateInventories() {
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
            s2c(player, REQUEST_REMOVE, buf)
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
            val optional = world.recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInv, world)
            if (optional.isPresent) {
                val craftingRecipe = optional.get()
                if (resultInv.shouldCraftRecipe(world, player, craftingRecipe)) {
                    itemStack = craftingRecipe.craft(craftingInv)
                }
            }
            outputSlot.setStack<WSlot>(itemStack)
            s2c(player, SLOT_UPDATE_PACKET, createSlotUpdatePacket(syncId, 0, 2, itemStack))
            resultInv.unlockLastRecipe(player)
        }
    }

    private fun sort() {
        if (world.isClient and initialized) screen { it.sort() }
    }

    private fun screen(x: (RequestScreen<*>) -> Any) {
        if (!world.isClient) return
        this as ScreenHandlerAccessor
        listeners.filterIsInstance<RequestScreen<*>>().forEach { x.invoke(it) }
    }

    override fun sendContentUpdates() {
        if (initialized) super.sendContentUpdates()
    }

    override fun onContentChanged(inventory: Inventory) {
        if ((inventory == craftingInv)) {
            craftItem()
        } else super.onContentChanged(inventory)
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

        val source: WSlot = `interface`.getSlot(inventoryNumber, slotNumber) ?: return
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
                val buffer = `interface`.getSlot<WSlot>(inventoryNumber, 0)
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
        s2c(player, REQUEST_CURSOR, buf)
    }

    override fun close(player: PlayerEntity) {
        clearCraft()
        dropInventory(player, world, craftingInv)
        master?.watchers?.remove(this)
        master?.unloadForcedChunks(world)
        super.close(player)
    }

    override fun getInventory(inventoryNumber: Int): Inventory? {
        return if (world.isClient and (inventoryNumber >= 3)) syncedInventory else inventories[inventoryNumber]
    }

    override fun getInterface(): WInterface {
        return if (world.isClient) syncedInterface else serverInterface
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.REQUEST
    }

    override fun onMasterRemoved() {
        if (player is ServerPlayerEntity) player.closeHandledScreen()
        screen { it.onClose() }
    }

}
