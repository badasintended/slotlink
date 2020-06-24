package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import badasintended.slotlink.common.texture
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WVerticalSlider
import spinnery.widget.api.Style

/**
 * Just a slider that wants to be a scrollbar.
 * Everybody says it's impossible for a mere slider to be a part of
 * the royal family of scrollbars but she's so stubborn.
 * Her journey seems too hard and even she thought of giving up
 * until she met the prince of the Scrollbar Kingdom...
 */
@Environment(EnvType.CLIENT)
class WFakeScrollBar(
    private val scrollFunction: (Int) -> Unit
) : WVerticalSlider() {

    override fun draw() {
        if (isHidden) return

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))

        // why
        val x = x.toDouble()
        val y = y.toDouble()
        val z = z.toDouble()
        val w = width.toDouble()
        val h = height.toDouble()

        BaseRenderer.drawBeveledPanel(
            x, y, z, w, h,
            slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"),
            slotStyle.asColor("bottom_right")
        )

        if (max == 0f) {
            BaseRenderer.drawImage(
                (x + 1), (y + 1), (z + 2), 12.0, 15.0,
                texture("gui/knob_off")
            )
            return
        }

        val knobY = y + 1 + ((h - 17) / (max) * (max - progress))

        BaseRenderer.drawImage(
            (x + 1), knobY, (z + 2), 12.0, 15.0,
            texture("gui/knob")
        )
    }

    override fun updatePosition(mouseX: Float, mouseY: Float) {
        super.updatePosition(mouseX, mouseY)
        scrollFunction.invoke((max - progress).toInt())
    }

}
