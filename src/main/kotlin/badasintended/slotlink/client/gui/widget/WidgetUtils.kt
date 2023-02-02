package badasintended.slotlink.client.gui.widget

import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack

inline fun <T> ClickableWidget.bounds(action: (Int, Int, Int, Int) -> T): T {
    return action(x, y, width, height)
}

interface KeyGrabber {

    fun onKey(keyCode: Int, scanCode: Int, modifiers: Int): Boolean

}

interface CharGrabber {

    fun onChar(chr: Char, modifiers: Int): Boolean

}

interface TooltipRenderer {

    fun renderTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int)

}