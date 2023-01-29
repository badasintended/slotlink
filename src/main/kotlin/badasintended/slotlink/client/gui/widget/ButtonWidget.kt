package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.GuiTextures
import badasintended.slotlink.client.util.bind
import badasintended.slotlink.client.util.client
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class ButtonWidget(x: Int, y: Int, w: Int, h: Int = w) : ClickableWidget(x, y, w, h, ScreenTexts.EMPTY) {

    var texture = GuiTextures.FILTER
    var onPressed = { }
    var bgU = 0
    var bgV = 0
    var u = { 0 }
    var v = { 0 }
    var background = true
    var allowSpectator = false
    var tooltip: () -> Text? = { null }

    private var lastTooltip: Text? = null
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
        val tooltip = this.tooltip()
        if (tooltip != lastTooltip) {
            setTooltip(Tooltip.of(tooltip))
            lastTooltip = tooltip
        }

        if (!visible) return
        texture.bind()

        if (background) {
            val u = if (hovered) bgU + width else bgU
            drawTexture(matrices, x, y, u, bgV, width, height)
        }

        padding.apply {
            drawTexture(matrices, x + l, y + t, u(), v(), width - l - r, height - t - b)
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!allowSpectator) {
            val player = client.player
            if (player != null && player.isSpectator) return false
        }

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

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {}

}
