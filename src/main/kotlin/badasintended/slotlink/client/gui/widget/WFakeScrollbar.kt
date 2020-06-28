package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Style
import kotlin.math.floor

/**
 * Just a slider that wants to be a scrollbar.
 * Everybody says it's impossible for a mere slider to be a part of
 * the royal family of scrollbars but she's so stubborn.
 * Her journey seems too hard and even she thought of giving up
 * until she met the prince of the Scrollbar Kingdom...
 */
@Environment(EnvType.CLIENT)
class WFakeScrollbar(
    private val scrollFunction: (Int) -> Unit
) : WVerticalSlider() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        // why
        val x = floor(x)
        val y = floor(y)
        val z = floor(z)
        val w = floor(width)
        val h = floor(height)

        BaseRenderer.drawBeveledPanel(
            matrices, provider,
            x, y, z, w, h,
            slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"),
            slotStyle.asColor("bottom_right")
        )

        val knobY = floor(y + 1 + if (max > 0f) ((h - 17) / (max) * (max - progress)) else 0f)

        BaseRenderer.drawBeveledPanel(
            matrices, provider,
            (x + 1), knobY, (z + 2), 12f, 15f,
            panelStyle.asColor("highlight"),
            if (max == 0f) slotStyle.asColor("background.unfocused") else panelStyle.asColor("background"),
            panelStyle.asColor("shadow")
        )

        for (i in 1..6) {
            BaseRenderer.drawQuad(
                matrices, provider,
                (x + 3), (knobY + (i * 2)), (z + 3), 8f, 1f,
                if (max == 0f) panelStyle.asColor("shadow") else slotStyle.asColor("background.unfocused")
            )
        }
    }

    override fun updatePosition(mouseX: Float, mouseY: Float) {
        super.updatePosition(mouseX, mouseY)
        scrollFunction.invoke((max - progress).toInt())
    }

}
