package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.gui.screen.RequestScreenHandler
import badasintended.slotlink.registry.NetworkRegistry.CRAFT_ONCE
import badasintended.slotlink.registry.NetworkRegistry.CRAFT_STACK
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import sbinnery.common.registry.NetworkRegistry.SLOT_CLICK_PACKET
import sbinnery.common.registry.NetworkRegistry.createSlotClickPacket
import sbinnery.common.utility.StackUtilities.equalItemAndTag
import sbinnery.widget.WSlot
import sbinnery.widget.api.Action.CLONE

@Environment(EnvType.CLIENT)
class WCraftingResultSlot : WSlot() {

    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused) return

        val container = `interface`.handler as RequestScreenHandler
        val player = container.player

        val cursorStack = player.inventory.cursorStack

        if (Screen.hasShiftDown()) {
            container.craftStack()
            if (button == LEFT) c2s(CRAFT_STACK, buf())
        } else {
            if ((button == LEFT) or (button == RIGHT)) {
                if ((!equalItemAndTag(
                        cursorStack, stack
                    ) and !cursorStack.isEmpty) or ((cursorStack.count + stack.count) > cursorStack.maxCount)
                ) return
                container.craftOnce()
                c2s(CRAFT_ONCE, buf())
            } else if (button == MIDDLE) {
                container.onSlotAction(slotNumber, inventoryNumber, button, CLONE, player)
                c2s(
                    SLOT_CLICK_PACKET,
                    createSlotClickPacket(container.syncId, slotNumber, inventoryNumber, button, CLONE)
                )
            }
        }
    }

}
