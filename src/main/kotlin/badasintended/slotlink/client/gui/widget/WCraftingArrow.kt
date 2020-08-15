package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.spinneryId
import badasintended.slotlink.common.util.tex
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import sbinnery.client.render.BaseRenderer
import sbinnery.common.registry.ThemeRegistry
import sbinnery.widget.WAbstractWidget
import sbinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WCraftingArrow : WAbstractWidget() {

    private val texture = tex("gui/arrow")

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, 22f, 15f, tint, texture)
    }

}
