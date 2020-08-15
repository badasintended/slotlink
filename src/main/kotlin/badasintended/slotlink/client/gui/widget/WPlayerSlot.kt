package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen.hasControlDown
import net.minecraft.client.gui.screen.Screen.hasShiftDown
import net.minecraft.item.ItemStack
import sbinnery.common.utility.MouseUtilities.nanoDelay
import sbinnery.common.utility.MouseUtilities.nanoInterval
import sbinnery.widget.WSlot

@Environment(EnvType.CLIENT)
class WPlayerSlot(
    private val putSameItem: (ItemStack) -> Unit
) : WSlot() {

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused || isLocked()) return

        if (!(nanoInterval() < nanoDelay() * 1.25f && button == LEFT) and hasShiftDown() and hasControlDown()) {
            putSameItem.invoke(stack)
        } else {
            super.onMouseClicked(mouseX, mouseY, button)
        }

        if (isWithinBounds(mouseX, mouseY)) {
            held = true
            heldSince = System.currentTimeMillis()
        }
    }

}
