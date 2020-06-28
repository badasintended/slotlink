package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.SortBy
import badasintended.slotlink.common.drawTintedImage
import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WAbstractButton
import spinnery.widget.WButton
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSortButton(
    private var sortImage: Identifier,
    private val sortFunction: () -> SortBy
) : WButton() {

    override fun draw() {
        val x = floor(x).toDouble()
        val y = floor(y).toDouble()
        val z = floor(z).toDouble()
        val w = floor(width).toDouble()
        val h = floor(height).toDouble()

        val panelStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("panel")))

        BaseRenderer.drawBeveledPanel(
            x, y, z, w, h,
            panelStyle.asColor("highlight"),
            panelStyle.asColor("background"),
            panelStyle.asColor("shadow")
        )

        val tint = panelStyle.asColor("label.color").RGB
        drawTintedImage(sortImage, tint, x, y, z, w, h)
    }

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) sortImage = sortFunction.invoke().texture
        return super.setLowered(toggleState)
    }

}
