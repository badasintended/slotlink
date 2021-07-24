package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.bindGuiTexture
import badasintended.slotlink.client.util.drawNinePatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class ScrollBarWidget(x: Int, y: Int, h: Int) : NoSoundWidget(x, y, 14, h.coerceAtLeast(17)) {

    var hasKnob = { true }

    var onUpdated: (Float) -> Unit = {}

    var knob = 0f

    private var knobY = 1
    private var clicked = false

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        bindGuiTexture()

        drawNinePatch(matrices, x, y, width, height, 16f, 0f, 1, 14)

        knobY = y + 1 + ((height - 17) * knob).toInt()

        val u = if (hasKnob.invoke()) 0 else 12

        drawTexture(matrices, x + 1, knobY, u, 16, 12, 15)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        clicked = hasKnob.invoke()
        return visible && isMouseOver(mouseX, mouseY)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (clicked) {
            knob = 1.0f - ((y + height - mouseY).toFloat() / height).coerceIn(0f, 1f)
            onUpdated.invoke(knob)
        }
        return visible && isMouseOver(mouseX, mouseY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        clicked = false
        return false
    }

}
