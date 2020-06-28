package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSlotArea : WMouseArea() {

    override fun draw() {
        val x = floor(x).toDouble()
        val y = floor(y).toDouble()
        val z = floor(z).toDouble()
        val w = floor(width).toDouble()
        val h = floor(height).toDouble()

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        BaseRenderer.drawBeveledPanel(
            x, y, z, w, h,
            slotStyle.asColor("top_left"),
            panelStyle.asColor("shadow"),
            slotStyle.asColor("bottom_right")
        )
    }

}
