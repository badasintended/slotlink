package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.TranslatableText
import spinnery.client.render.TextRenderer
import spinnery.widget.WStaticText
import spinnery.widget.api.Color

@Environment(EnvType.CLIENT)
class WTranslatableLabel(key: String, vararg args: Any) : WStaticText() {

    init {
        setText<WStaticText>(TranslatableText(key, *args))
    }

    override fun draw() {
        if (isHidden) return

        TextRenderer.pass()
            .text(text).font(font)
            .at(x, y, z).scale(scale).maxWidth(maxWidth)
            .shadow(false)
            .color(Color.of(0x404040))
            .render()
    }

}
