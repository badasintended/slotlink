package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.slotAction
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import spinnery.widget.WSlot
import spinnery.widget.api.Action
import spinnery.widget.api.Action.CLONE
import spinnery.widget.api.Action.PICKUP

@Environment(EnvType.CLIENT)
class WMultiSlot(
    private val actionPerformed: (Boolean) -> Unit,
    private val sort: () -> Unit
) : WSlot() {

    private val linkedSlots = arrayListOf<WLinkedSlot>()

    fun setLinkedSlots(vararg serverSlot: WLinkedSlot) {
        linkedSlots.clear()
        linkedSlots.addAll(serverSlot)
        linkedSlots.sortByDescending { it.stack.count }
    }

    /**
     * Drag event shouldn't happen here
     */
    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        `interface`.container.flush()
        skipRelease = false
        isHeld = false
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused or isLocked) return

        val container = `interface`.container
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        val slot = linkedSlots.first()
        val sSlotN = slot.slotNumber
        val sSlotInvN = slot.invNumber

        if (Screen.hasShiftDown()) {
            if (button == LEFT) {
                linkedSlots.forEach {
                    slotAction(container, it.slotNumber, it.invNumber, button, Action.QUICK_MOVE, player)
                }
                actionPerformed.invoke(true)
                sort.invoke()
            }
        } else {
            if ((button == LEFT) or (button == RIGHT) and isCursorEmpty) {
                skipRelease = true
                slotAction(container, sSlotN, sSlotInvN, button, PICKUP, player)
                actionPerformed.invoke(true)
                sort.invoke()
            } else if (button == MIDDLE) {
                slotAction(container, sSlotN, sSlotInvN, button, CLONE, player)
                actionPerformed.invoke(true)
                sort.invoke()
            }
        }


        if (isWithinBounds(mouseX, mouseY)) {
            isHeld = true
            heldSince = System.currentTimeMillis()
            sort.invoke()
        }
    }


}
