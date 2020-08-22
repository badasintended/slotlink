package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.util.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import sbinnery.client.render.BaseRenderer
import sbinnery.common.registry.ThemeRegistry
import sbinnery.widget.WAbstractButton
import sbinnery.widget.WButton
import sbinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WIconButton(
    tlKey: String, private val texture: Identifier, private val click: () -> Unit
) : WButton() {

    private val tooltip = arrayListOf<Text>(
        TranslatableText(tlKey).formatted(Formatting.GRAY)
    )

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, texture)
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) click.invoke()
        return super.setLowered(toggleState)
    }

    override fun getTooltip() = tooltip

}
