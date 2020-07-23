package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.slotAction
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import spinnery.common.registry.NetworkRegistry.SLOT_DRAG_PACKET
import spinnery.common.registry.NetworkRegistry.createSlotDragPacket
import spinnery.common.utility.MouseUtilities.*
import spinnery.widget.api.Action.*

/**
 * Bruh sometimes, really rare case, sorting after every click crash the game
 * and the only solution that i can think of is this
 */
class WPlayerSlot(
    private val putSameItem: (ItemStack) -> Unit,
    private val sort: () -> Unit
) : WVanillaSlot() {

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused || isLocked()) return

        val container = `interface`.handler
        val playerInventory = container.playerInventory
        val player = playerInventory.player

        val isCursorEmpty = playerInventory.cursorStack.isEmpty

        if (nanoInterval() < nanoDelay() * 1.25f && button == LEFT) {
            skipRelease = true
            slotAction(container, slotNumber, inventoryNumber, button, PICKUP_ALL, player)
            sort.invoke()
        } else {
            nanoUpdate()
            if (Screen.hasShiftDown()) {
                if (button == LEFT) {
                    if (Screen.hasControlDown()) putSameItem.invoke(stack) else {
                        `interface`.cachedWidgets[javaClass] = this
                        slotAction(container, slotNumber, inventoryNumber, button, QUICK_MOVE, player)
                        sort.invoke()
                    }
                }
            } else if ((button == LEFT || button == RIGHT) && isCursorEmpty) {
                skipRelease = true
                slotAction(container, slotNumber, inventoryNumber, button, PICKUP, player)
                sort.invoke()
            } else if (button == MIDDLE) {
                slotAction(container, slotNumber, inventoryNumber, button, CLONE, player)
                sort.invoke()
            }
        }

        if (isWithinBounds(mouseX, mouseY)) {
            held = true
            heldSince = System.currentTimeMillis()
        }
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {
        if (!isFocused || button == MIDDLE || isLocked()) return

        val container = `interface`.handler
        val player = container.playerInventory.player

        val isCached = `interface`.cachedWidgets[javaClass] === this

        val slotNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.slotNumber }.toArray()
        val inventoryNumbers = container.getDragSlots(button).stream().mapToInt { obj -> obj.inventoryNumber }.toArray()

        if (Screen.hasShiftDown()) {
            if (button == LEFT && !isCached) {
                `interface`.cachedWidgets[javaClass] = this
                slotAction(container, slotNumber, inventoryNumber, button, QUICK_MOVE, player)
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

        val container = `interface`.handler
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
                slotAction(container, slotNumber, inventoryNumber, button, PICKUP, player)
                sort.invoke()
            }
        }

        container.flush()

        skipRelease = false
        held = false
    }

}

/**
 * yes, i literally copied all of mouse event.
 */
