package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.common.registry.NetworkRegistry.*
import badasintended.spinnery.common.utility.MouseUtilities.*
import badasintended.spinnery.widget.WSlot
import badasintended.spinnery.widget.api.Action.*
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
import net.minecraft.client.gui.screen.Screen

/**
 * Bruh sometimes, really rare case, sorting after every click crash the game
 * and the only solution that i can think of is this
 */
class WPlayerSlot(
    private val sort: () -> Unit
) : WSlot() {

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused || isLocked()) return

        val container = `interface`.container
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        if (nanoInterval() < nanoDelay() * 1.25f && button == LEFT) {
            skipRelease = true
            container.onSlotAction(slotNumber, inventoryNumber, button, PICKUP_ALL, player)
            INSTANCE.sendToServer(
                SLOT_CLICK_PACKET,
                createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, PICKUP_ALL)
            )
            sort.invoke()
        } else {
            nanoUpdate()
            if (Screen.hasShiftDown()) {
                if (button == LEFT) {
                    `interface`.cachedWidgets[javaClass] = this
                    container.onSlotAction(slotNumber, inventoryNumber, button, QUICK_MOVE, player)
                    INSTANCE.sendToServer(
                        SLOT_CLICK_PACKET,
                        createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, QUICK_MOVE)
                    )
                    sort.invoke()
                }
            } else if ((button == LEFT || button == RIGHT) && isCursorEmpty) {
                skipRelease = true
                container.onSlotAction(slotNumber, inventoryNumber, button, PICKUP, player)
                INSTANCE.sendToServer(
                    SLOT_CLICK_PACKET,
                    createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, PICKUP)
                )
                sort.invoke()
            } else if (button == MIDDLE) {
                container.onSlotAction(slotNumber, inventoryNumber, button, CLONE, player)
                INSTANCE.sendToServer(
                    SLOT_CLICK_PACKET,
                    createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, CLONE)
                )
                sort.invoke()
            }
        }

        if (isWithinBounds(mouseX, mouseY)) {
            isHeld = true
            heldSince = System.currentTimeMillis()
        }
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {
        if (!isFocused || button == MIDDLE || isLocked()) return

        val container = `interface`.container
        val player = container.playerInventory.player

        val isCached = `interface`.cachedWidgets[javaClass] === this

        val slotNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.slotNumber }.toArray()
        val inventoryNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.inventoryNumber }.toArray()

        if (Screen.hasShiftDown()) {
            if (button == LEFT && !isCached) {
                `interface`.cachedWidgets[javaClass] = this
                container.onSlotAction(slotNumber, inventoryNumber, button, QUICK_MOVE, player)
                INSTANCE.sendToServer(
                    SLOT_CLICK_PACKET,
                    createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, QUICK_MOVE)
                )
                sort.invoke()
            }
        } else {
            if ((button == LEFT || button == RIGHT) && nanoInterval() > nanoDelay()) {
                if (container.getDragSlots(button).isNotEmpty()) {
                    val stackA = container.getDragSlots(button).iterator().next().stack
                    val stackB = stack
                    if (stackA.item !== stackB.item || stackA.tag !== stackB.tag) return
                }
                container.getDragSlots(button).add(this)
                container.onSlotDrag(slotNumbers, inventoryNumbers, of(button, false))
            }
        }
    }

    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        if (button == MIDDLE || isLocked()) return

        val container = `interface`.container
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val slotNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.slotNumber }.toArray()
        val inventoryNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.inventoryNumber }.toArray()

        val isDragging = container.isDragging && nanoInterval() > nanoDelay()
        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        if (!skipRelease && !Screen.hasShiftDown()) {
            if (isDragging) {
                container.onSlotDrag(slotNumbers, inventoryNumbers, of(button, true))
                INSTANCE.sendToServer(
                    SLOT_DRAG_PACKET,
                    createSlotDragPacket(container.syncId, slotNumbers, inventoryNumbers, of(button, true))
                )
                sort.invoke()
            } else if (!isFocused) return else if ((button == LEFT || button == RIGHT) && !isCursorEmpty) {
                container.onSlotAction(slotNumber, inventoryNumber, button, PICKUP, player)
                INSTANCE.sendToServer(
                    SLOT_CLICK_PACKET,
                    createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, PICKUP)
                )
                sort.invoke()
            }
        }

        container.flush()

        skipRelease = false
        isHeld = false
    }

}

/**
 * yes, i literally copied all of mouse event.
 */
