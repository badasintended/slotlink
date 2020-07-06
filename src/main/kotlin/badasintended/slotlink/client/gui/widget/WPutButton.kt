package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.Mod
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WPutButton(
    private val tooltip: () -> Unit,
    private val click: () -> Unit
) : WButton() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, Mod.id("textures/gui/put.png"))

        if (isFocused) tooltip.invoke()
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) click.invoke()
        return super.setLowered(toggleState)
    }

}
