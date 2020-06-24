package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WTextField
import spinnery.widget.api.Style

@Environment(EnvType.CLIENT)
class WSearchBar(
    private val setSearch: (String) -> Unit
) : WTextField() {

    override fun draw() {
        if (isHidden) return

        val x = x.toDouble()
        val y = y.toDouble()
        val z = z.toDouble()
        val w = width.toDouble()
        val h = height.toDouble()

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))

        BaseRenderer.drawBeveledPanel(
            x, (y + 1), z, (w - 2), (h - 4),
            slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"),
            slotStyle.asColor("bottom_right")
        )

        renderField()
    }

    override fun onKeyReleased(keyCode: Int, character: Int, keyModifier: Int) {
        super.onKeyReleased(keyCode, character, keyModifier)
        setSearch.invoke(text)
    }

}
