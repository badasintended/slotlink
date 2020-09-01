package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.util.spinneryId
import badasintended.slotlink.util.tex
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import sbinnery.client.render.BaseRenderer
import sbinnery.common.registry.ThemeRegistry
import sbinnery.widget.WButton
import sbinnery.widget.api.Color
import sbinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSlotButton : WButton() {

    private var tlKey = { "" }
    private var texture = { tex("unknown") }
    private var tinted = true
    private var onClick = {}

    fun tlKey(v: () -> String): WSlotButton {
        tlKey = v
        return this
    }

    fun texture(v: () -> Identifier): WSlotButton {
        texture = v
        return this
    }

    fun tinted(v: Boolean): WSlotButton {
        tinted = v
        return this
    }

    fun onClick(v: () -> Unit): WSlotButton {
        onClick = v
        return this
    }

    fun tlKey(v: String) = tlKey { v }

    fun texture(v: Identifier) = texture { v }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        if (isLowered) BaseRenderer.drawBeveledPanel(
            matrices, provider, x, y, z, w, h, panelStyle.asColor("shadow"), slotStyle.asColor("background.unfocused"),
            panelStyle.asColor("highlight")
        ) else BaseRenderer.drawBeveledPanel(
            matrices, provider, x, y, z, w, h, panelStyle.asColor("highlight"), panelStyle.asColor("background"),
            panelStyle.asColor("shadow")
        )

        val tint = panelStyle.asColor("label.color")
        BaseRenderer.drawTexturedQuad(
            matrices, provider, x, y, z, w, h, if (tinted) tint else Color.of(0xFFFFFFFF), texture.invoke()
        )
    }

    override fun onMouseReleased(mouseX: Float, mouseY: Float, mouseButton: Int) {
        lowered = false
    }

    override fun onMouseClicked(mouseX: Float, mouseY: Float, mouseButton: Int) {
        onClick.invoke()
        lowered = true
    }

    override fun getTooltip() = listOf(TranslatableText(tlKey.invoke()))

}
