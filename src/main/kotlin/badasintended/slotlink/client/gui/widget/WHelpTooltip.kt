package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.spinneryId
import badasintended.slotlink.common.util.tex
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractWidget
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WHelpTooltip(key: String, size: Int) : WAbstractWidget() {

    private val tooltip = arrayListOf<Text>()

    init {
        for (i in 0 until size) tooltip.add(TranslatableText("$key.help$i"))
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, tex("gui/help"))
    }

    override fun getTooltip() = tooltip

}
