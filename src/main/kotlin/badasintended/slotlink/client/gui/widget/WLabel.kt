package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import sbinnery.client.render.TextRenderer
import sbinnery.common.registry.ThemeRegistry
import sbinnery.widget.WStaticText
import sbinnery.widget.api.Style

@Environment(EnvType.CLIENT)
open class WLabel : WStaticText() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))
        TextRenderer
            .pass()
            .text(text)
            .at(x, y, z)
            .shadow(false)
            .color(panelStyle.asColor("label.color"))
            .render(matrices, provider)
    }

}
