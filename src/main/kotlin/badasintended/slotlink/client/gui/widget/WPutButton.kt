package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.client.render.BaseRenderer
import badasintended.spinnery.common.registry.ThemeRegistry
import badasintended.spinnery.widget.WAbstractButton
import badasintended.spinnery.widget.WButton
import badasintended.spinnery.widget.api.Style
import badasintended.slotlink.Slotlink
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WPutButton(
    private val tooltip: (MatrixStack) -> Unit,
    private val click: () -> Unit
) : WButton() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, Slotlink.id("textures/gui/put.png"))

        if (isFocused) tooltip.invoke(matrices)
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) click.invoke()
        return super.setLowered(toggleState)
    }

}
