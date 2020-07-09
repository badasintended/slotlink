package badasintended.slotlink.client.gui.widget

import badasintended.spinnery.client.render.TextRenderer
import badasintended.spinnery.common.registry.ThemeRegistry
import badasintended.spinnery.widget.WStaticText
import badasintended.spinnery.widget.api.Style
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
class WTranslatableLabel(key: String, vararg args: Any) : WStaticText() {

    init {
        setText<WStaticText>(TranslatableText(key, *args))
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        TextRenderer.pass()
            .text(text)
            .at(x, y, z).scale(scale.toDouble()).maxWidth(maxWidth)
            .shadow(false)
            .color(panelStyle.asColor("label.color"))
            .render(matrices, provider)
    }

}
