package io.gitlab.intended.storagenetworks.gui.widget

import net.minecraft.text.TranslatableText
import spinnery.client.render.TextRenderer
import spinnery.widget.WStaticText
import spinnery.widget.api.Color

class WTranslatableLabel(key: String) : WStaticText() {

    init {
        setText<WStaticText>(TranslatableText(key))
    }

    override fun draw() {
        if (isHidden) return

        TextRenderer.pass()
            .text(text).font(font)
            .at(x, y, z).scale(scale).maxWidth(maxWidth)
            .shadow(false)
            .color(style.asColor("label.color", Color.of(0x404040)))
            .render()
    }

}
