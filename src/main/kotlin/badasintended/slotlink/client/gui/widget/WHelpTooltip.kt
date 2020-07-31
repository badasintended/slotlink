package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.Slotlink
import badasintended.slotlink.common.util.spinneryId
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.*
import net.minecraft.util.Formatting
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractWidget
import spinnery.widget.api.Style
import kotlin.math.floor

class WHelpTooltip : WAbstractWidget() {

    private val tooltip = arrayListOf<Text>(
        TranslatableText("block.slotlink.request.help1"),
        TranslatableText("block.slotlink.request.help2").formatted(Formatting.GRAY),
        TranslatableText("block.slotlink.request.help3").formatted(Formatting.GRAY),
        LiteralText(""),
        TranslatableText("block.slotlink.request.help4"),
        TranslatableText("block.slotlink.request.help5").formatted(Formatting.GRAY),
        TranslatableText("block.slotlink.request.help6").formatted(Formatting.GRAY)
    )

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme,
            spinneryId("slot")
        ))
        val tint = slotStyle.asColor("background.unfocused")

        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, Slotlink.id("textures/gui/help.png"))
    }

    override fun getTooltip() = tooltip

}
