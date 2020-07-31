package badasintended.slotlink.gui.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.common.util.buf
import badasintended.slotlink.common.util.SortBy
import badasintended.slotlink.common.registry.NetworkRegistry.REQUEST_REMOVE
import badasintended.slotlink.common.registry.NetworkRegistry.REQUEST_SYNC
import badasintended.slotlink.gui.widget.WServerSlot
import badasintended.slotlink.inventory.DummyInventory
import badasintended.slotlink.mixin.ScreenHandlerAccessor
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.*
import net.minecraft.item.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import spinnery.common.registry.NetworkRegistry.SLOT_UPDATE_PACKET
import spinnery.common.registry.NetworkRegistry.createSlotUpdatePacket
import spinnery.common.utility.StackUtilities
import spinnery.widget.WSlot
import spinnery.widget.api.Action
import spinnery.widget.api.Action.*
import spinnery.widget.api.Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK
import spinnery.widget.api.Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


open class RequestScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModScreenHandler(syncId, player) {

    val blockPos: BlockPos = buf.readBlockPos()
    var lastSort = SortBy.of(buf.readInt())

    private val dimId = buf.readIdentifier()

    private val masterPos = buf.readBlockPos()

    private val inventoryPos = arrayListOf<BlockPos>()
    private val inventoryStates = arrayListOf<BlockState>()

    private val masterWorld: ServerWorld?

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val inputSlots = arrayListOf<WSlot>()
    private val outputSlot: WSlot

    private val invMap = HashMap<Int, Inventory>()

    val playerSlots = arrayListOf<WSlot>()
    val linkedSlots = arrayListOf<WSlot>()

    private val buffer2: WSlot
    private val buffer3: WSlot

    init {
        if (!world.isClient) {
            masterWorld = world.server!!.getWorld(RegistryKey.of(Registry.DIMENSION, dimId))
            if (masterWorld != null) initServer()
        } else masterWorld = null

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

        for (i in 0 until 9) {
            val slot = root.createChild { WServerSlot(this::sort) }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
            inputSlots.add(slot)
        }

        outputSlot = root.createChild { WServerSlot(this::sort) }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        for (i in 0 until 36) {
            val slot = root.createChild { WServerSlot(this::sort) }
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(i)
            playerSlots.add(slot)
        }
    }

    private fun initServer() {
        masterWorld!!

        val masterBlockEntity = masterWorld.getBlockEntity(masterPos) ?: return
        if (masterBlockEntity !is MasterBlockEntity) return

        var i = 3
        masterBlockEntity.getLinkedInventories(masterWorld).forEach { (linkedPos, linkedInv) ->
            inventoryPos.add(linkedPos)
            inventoryStates.add(masterWorld.getBlockState(linkedPos))
            invMap[i] = linkedInv
            i++
        }

        val buf = buf()

        buf.writeInt(invMap.size)
        invMap.forEach { (num, inv) ->
            buf.writeInt(num)
            buf.writeInt(inv.size())
            buf.writeInt(inv.maxCountPerStack)
            for (j in 0 until inv.size()) {
                buf.writeItemStack(inv.getStack(j))
            }
        }

        createSlots(invMap)
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, REQUEST_SYNC, buf)
    }

    fun createSlots(map: Map<Int, Inventory>) {
        invMap.putAll(map)
        inventories.putAll(map)

        map.forEach { (num, inv) ->
            for (i in 0 until inv.size()) {
                val slot = root.createChild { WServerSlot(this::sort) }
                slot.setInventoryNumber<WSlot>(num)
                slot.setSlotNumber<WSlot>(i)
                slot.setMaximumCount<WSlot>(inv.maxCountPerStack)
                linkedSlots.add(slot)
            }
        }

        this as ScreenHandlerAccessor
        sort()
    }

    fun validateInventories() {
        if (world.isClient) return
        if (masterWorld == null) return

        val buf = buf()
        val deletedNumbers = hashSetOf<Int>()

        invMap.forEach { (i, _) ->
            val state = masterWorld.getBlockState(inventoryPos[i - 3])
            val deleted = state != inventoryStates[i - 3]
            if (deleted) {
                deletedNumbers.add(i)
                linkedSlots.removeIf { it.inventoryNumber == i }
            }
        }

        buf.writeIntArray(deletedNumbers.toIntArray())
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, REQUEST_REMOVE, buf)
    }

    fun craftOnce() {
        val output = outputSlot.stack
        val cursor = playerInventory.cursorStack
        if (!StackUtilities.equalItemAndTag(output, cursor) and !cursor.isEmpty) return
        if ((output.count + cursor.count) > output.maxCount) return

        validateInventories()

        craftInternal()

        val buffer = buffer3.stack
        StackUtilities.merge(buffer, cursor, buffer.maxCount, cursor.maxCount)
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

        StackUtilities.merge(outputSlot.stack, buffer3.stack, outputSlot.stack.maxCount, buffer3.stack.maxCount)
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
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                player, SLOT_UPDATE_PACKET,
                createSlotUpdatePacket(syncId, outputSlot.slotNumber, outputSlot.inventoryNumber, itemStack)
            )
            resultInv.unlockLastRecipe(player)
        }
    }

    private fun sort() {
        this as ScreenHandlerAccessor
        listeners.filterIsInstance<RequestScreen<*>>().forEach { it.sort() }
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
        slotNumber: Int,
        inventoryNumber: Int,
        button: Int,
        action: Action,
        player: PlayerEntity
    ) {
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
                    StackUtilities.merge(source::getStack, target::getStack, source::getMaxCount) { max }
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
                    StackUtilities.merge(slot::getStack, { cursorStack }, slot::getMaxCount, { cursorStack.maxCount }
                    ).apply({ slot.setStack<WSlot>(it) }, { playerInventory.cursorStack = it })
                }
            }
        } else super.onSlotAction(slotNumber, inventoryNumber, button, action, player)
    }

    override fun close(player: PlayerEntity) {
        clearCraft()
        dropInventory(player, world, craftingInv)
        super.close(player)
    }

}
