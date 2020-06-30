package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.TranslatableText
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WTextField
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSearchBar(
    private val setSearch: (String) -> Unit,
    private val drawTooltip: () -> Unit
) : WTextField() {

    init {
        setLabel<WSearchBar>(TranslatableText("block.slotlink.request.search"))
    }

    override fun draw() {
        if (isHidden) return

        val x = floor(x.toDouble())
        val y = floor(y.toDouble())
        val z = floor(z.toDouble())
        val w = floor(width.toDouble())
        val h = floor(height.toDouble())

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))

        BaseRenderer.drawBeveledPanel(
            x, (y + 1), z, (w - 2), (h - 4),
            slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"),
            slotStyle.asColor("bottom_right")
        )

        if (isFocused and !isActive) drawTooltip.invoke()

        renderField()
    }

    override fun onKeyReleased(keyCode: Int, character: Int, keyModifier: Int) {
        super.onKeyReleased(keyCode, character, keyModifier)
        setSearch.invoke(text)
    }

}
