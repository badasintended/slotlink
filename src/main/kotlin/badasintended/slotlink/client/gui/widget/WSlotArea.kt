package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.client.render.BaseRenderer
import badasintended.spinnery.common.registry.ThemeRegistry
import badasintended.spinnery.widget.api.Style
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSlotArea : WMouseArea() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        BaseRenderer.drawBeveledPanel(
            matrices, provider,
            x, y, z, w, h,
            slotStyle.asColor("top_left"),
            panelStyle.asColor("shadow"),
            slotStyle.asColor("bottom_right")
        )
    }

}
