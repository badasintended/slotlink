package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.Mod
import badasintended.slotlink.common.drawTintedImage
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractWidget
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WCraftingArrow : WAbstractWidget() {

    private val texture = Mod.id("textures/gui/arrow.png")

    override fun draw() {
        val x = floor(x).toDouble()
        val y = floor(y).toDouble()
        val z = floor(z).toDouble()

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))
        val tint = slotStyle.asColor("background.unfocused").RGB

        drawTintedImage(texture, tint, x, y, z, 22.0, 15.0)
    }

}
