package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.Mod
import badasintended.slotlink.common.drawTintedImage
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WPutButton(
    private val tooltip: () -> Unit,
    private val click: () -> Unit
) : WButton() {

    override fun draw() {
        val x = floor(x).toDouble()
        val y = floor(y).toDouble()
        val z = floor(z).toDouble()
        val w = floor(width).toDouble()
        val h = floor(height).toDouble()

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused").RGB

        drawTintedImage(Mod.id("textures/gui/put.png"), tint, x, y, z, w, h)

        if (isFocused) tooltip.invoke()
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) click.invoke()
        return super.setLowered(toggleState)
    }

}
