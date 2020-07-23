package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.SortBy
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSortButton(
    private var sort: SortBy,
    private val sortFunction: () -> SortBy
) : WButton() {

    private val tooltip = arrayListOf<Text>(
        TranslatableText(sort.translationKey).formatted(Formatting.GRAY)
    )

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider) {
        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        if (isLowered) BaseRenderer.drawBeveledPanel(
            matrices, provider,
            x, y, z, w, h,
            panelStyle.asColor("shadow"),
            slotStyle.asColor("background.unfocused"),
            panelStyle.asColor("highlight")
        ) else BaseRenderer.drawBeveledPanel(
            matrices, provider,
            x, y, z, w, h,
            panelStyle.asColor("highlight"),
            panelStyle.asColor("background"),
            panelStyle.asColor("shadow")
        )

        val tint = panelStyle.asColor("label.color")
        BaseRenderer.drawTexturedQuad(matrices, provider, x, y, z, w, h, tint, sort.texture)
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) {
            sort = sortFunction.invoke()
            tooltip[0] = TranslatableText(sort.translationKey).formatted(Formatting.GRAY)
        }
        return super.setLowered(toggleState)
    }

    override fun getTooltip() = tooltip

}
