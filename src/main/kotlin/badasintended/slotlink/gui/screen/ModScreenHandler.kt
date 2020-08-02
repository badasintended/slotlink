package badasintended.slotlink.gui.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import spinnery.common.handler.BaseScreenHandler
import spinnery.common.utility.MutablePair
import spinnery.common.utility.StackUtilities
import spinnery.widget.WInterface
import spinnery.widget.WSlot
import spinnery.widget.api.Action
import java.util.*

abstract class ModScreenHandler(
    syncId: Int, playerInventory: PlayerInventory
) : BaseScreenHandler(syncId, playerInventory) {

    val player: PlayerEntity = playerInventory.player

    protected val root: WInterface = `interface`

    private val fixedSplitSlots = linkedSetOf<WSlot>()
    private val fixedSingleSlots = linkedSetOf<WSlot>()

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
                stackA, stackB, split, stackA.maxCount.coerceAtMost(split + stackB.count)
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

    abstract override fun getType(): ScreenHandlerType<*>

}
