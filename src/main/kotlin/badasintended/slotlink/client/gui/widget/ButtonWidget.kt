package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.bindGuiTexture
import badasintended.slotlink.client.util.drawNinePatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

@Environment(EnvType.CLIENT)
class ButtonWidget(x: Int, y: Int, w: Int, h: Int = w) : ClickableWidget(x, y, w, h, LiteralText.EMPTY) {

    var onHovered: (MatrixStack, Int, Int) -> Unit = { _, _, _ -> }
    var onPressed = { }
    var u = { 0 }
    var v = { 0 }
    var background = true
    var outline = false

    private var down = false
    private val padding = object {
        var l = 0
        var r = 0
        var t = 0
        var b = 0
    }

    fun padding(l: Int, t: Int = l, r: Int = l, b: Int = t) {
        padding.l = l
        padding.r = r
        padding.t = t
        padding.b = b
    }

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        bindGuiTexture()

        if (background) {
            if (outline) {
                val u = if (hovered) 64f else 48f
                drawNinePatch(matrices, x, y, width, height, u, 16f, 2, 12)
            } else {
                val u = if (down) 16f else if (hovered) 48f else 32f
                drawNinePatch(matrices, x, y, width, height, u, 0f, 1, 14)
            }
        }

        padding.apply {
            drawTexture(matrices, x + l, y + t, u(), v(), width - l - r, height - t - b)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isMouseOver(mouseX, mouseY)) {
            down = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (down) onPressed()
        down = false
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {}

    override fun renderTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (visible) onHovered.invoke(matrices, mouseX, mouseY)
    }

}
