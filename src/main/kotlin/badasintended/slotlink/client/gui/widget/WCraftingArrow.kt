package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.client.render.BaseRenderer
import badasintended.spinnery.common.registry.ThemeRegistry
import badasintended.spinnery.widget.WAbstractWidget
import badasintended.spinnery.widget.api.Style
import badasintended.slotlink.Slotlink
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WCraftingArrow : WAbstractWidget() {

    private val texture = Slotlink.id("textures/gui/arrow.png")

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, 22f, 15f, tint, texture)

        //drawTintedImage(texture, tint, x, y, z, 22.0, 15.0)
    }

}
