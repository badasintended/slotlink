package badasintended.slotlink.screen

import badasintended.slotlink.client.gui.screen.AbstractRequestScreen
import badasintended.slotlink.common.SortBy
import badasintended.slotlink.common.hasInv
import badasintended.slotlink.common.isInvProvider
import badasintended.slotlink.inventory.DummyInventory
import badasintended.slotlink.mixin.ScreenHandlerAccessor
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import spinnery.common.registry.NetworkRegistry.SLOT_UPDATE_PACKET
import spinnery.common.registry.NetworkRegistry.createSlotUpdatePacket
import spinnery.common.utility.MutablePair
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


abstract class AbstractRequestScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModScreenHandler(syncId, player) {

    val blockPos: BlockPos = buf.readBlockPos()
    var lastSort = SortBy.of(buf.readInt())

    private val hasMaster = buf.readBoolean()

    private val totalInventory = if (hasMaster) buf.readInt() else 0

    private val inventoryPos = arrayListOf<BlockPos>()
    private val inventoryStates = arrayListOf<BlockState>()

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val inputSlots = arrayListOf<WSlot>()
    private val outputSlot: WSlot

    private val invMap = HashMap<Int, Inventory>()

    val playerSlots = arrayListOf<WSlot>()
    val linkedSlots = arrayListOf<WSlot>()

    private val buffer2: WSlot
    private val buffer3: WSlot

    private val fixedSplitSlots = linkedSetOf<WSlot>()
    private val fixedSingleSlots = linkedSetOf<WSlot>()

    init {
        for (i in 0 until totalInventory) inventoryPos.add(buf.readBlockPos())

        inventoryPos.forEachIndexed { index, blockPos ->
            val chunk = world.getWorldChunk(blockPos)
            val blockState = chunk.getBlockState(blockPos)
            val block = blockState.block
            val blockEntity = chunk.getBlockEntity(blockPos)
            inventoryStates.add(chunk.getBlockState(blockPos))
            if (block.isInvProvider()) {
                block as InventoryProvider
                invMap[index + 3] = block.getInventory(blockState, world, blockPos)
            } else if (blockEntity.hasInv()) {
                invMap[index + 3] = blockEntity as Inventory
            }
        }

        inventories[-3] = DummyInventory(1, 1)
        inventories[-2] = DummyInventory(1, 1)
        inventories[-1] = DummyInventory(8, 6)
        inventories[1] = craftingInv
        inventories[2] = resultInv
        inventories.putAll(invMap)

        // buffers
        WSlot.addHeadlessArray(root, 0, -1, 8, 6)

        buffer2 = root.createChild { WSlot() }
        buffer2.setInventoryNumber<WSlot>(-2)
        buffer2.setSlotNumber<WSlot>(0)

        buffer3 = root.createChild { WSlot() }
        buffer3.setInventoryNumber<WSlot>(-3)
        buffer3.setSlotNumber<WSlot>(0)

        for (i in 0 until 9) {
            val slot = root.createChild { WSlot() }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
            inputSlots.add(slot)
        }

        invMap.forEach { (num, inv) ->
            for (i in 0 until inv.size()) {
                val slot = root.createChild { WSlot() }
                slot.setInventoryNumber<WSlot>(num)
                slot.setSlotNumber<WSlot>(i)
                slot.setMaximumCount<WSlot>(inv.maxCountPerStack)
                linkedSlots.add(slot)
            }
        }

        outputSlot = root.createChild { WSlot() }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        for (i in 0 until 36) {
            val slot = root.createChild { WSlot() }
            slot.setInventoryNumber<WSlot>(0)
            slot.setSlotNumber<WSlot>(i)
            playerSlots.add(slot)
        }
    }

    fun validateInventories() {
        invMap.forEach { (i, _) ->
            val state = world.getBlockState(inventoryPos[i - 3])
            val deleted = state != inventoryStates[i - 3]
            if (deleted) linkedSlots.removeIf { it.inventoryNumber == i }
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

    override fun onContentChanged(inventory: Inventory) {
        if ((inventory == craftingInv)) {
            craftItem()
        } else super.onContentChanged(inventory)
    }

    override fun getDragSlots(mouseButton: Int): MutableSet<WSlot>? {
        return when (mouseButton) {
            0 -> fixedSplitSlots
            1 -> fixedSingleSlots
            else -> null
        }
    }

    override fun onSlotDrag(slotNumber: IntArray, inventoryNumber: IntArray, action: Action) {
        val slots: MutableSet<WSlot> = LinkedHashSet()

        for (i in slotNumber.indices) {
            val slot = getInterface().getSlot<WSlot>(inventoryNumber[i], slotNumber[i])
            if (slot != null) slots.add(slot)
        }

        if (slots.isEmpty()) return

        val split = if (action.isSplit) (playerInventory.cursorStack.count / slots.size).coerceAtLeast(1) else 1

        var stackA = if (action.isPreview) playerInventory.cursorStack.copy() else playerInventory.cursorStack

        if (stackA.isEmpty) return

        for (slotA in slots) {
            if (slotA.refuses(stackA)) continue
            val stackB = if (action.isPreview) slotA.stack.copy() else slotA.stack
            val stacks: MutablePair<ItemStack, ItemStack> = StackUtilities.merge(
                stackA, stackB, split,
                stackA.maxCount.coerceAtMost(split + stackB.count)
            )
            if (action.isPreview) {
                previewCursorStack = stacks.first.copy()
                slotA.setPreviewStack<WSlot>(stacks.second.copy())
            } else {
                stackA = stacks.first
                previewCursorStack = ItemStack.EMPTY
                slotA.setStack(stacks.second)
            }
        }
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

        if (inventoryNumber == 0) listeners.forEach {
            if (it is AbstractRequestScreen<*>) it.onSlotUpdate(this, 0, ItemStack.EMPTY)
        }
    }

    override fun close(player: PlayerEntity) {
        clearCraft()
        dropInventory(player, world, craftingInv)
        super.close(player)
    }

}
