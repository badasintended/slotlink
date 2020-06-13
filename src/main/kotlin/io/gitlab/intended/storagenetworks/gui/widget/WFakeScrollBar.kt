package io.gitlab.intended.storagenetworks.gui.widget

import io.gitlab.intended.storagenetworks.guiTex
import spinnery.client.render.BaseRenderer
import spinnery.widget.WVerticalSlider

/**
 * Just a slider that wants to be a scrollbar.
 * Everybody says it's impossible for a mere slider to be a part of
 * the royal family of scrollbars but she's so stubborn.
 * Her journey seems too hard and even she thought of giving up
 * until she met the prince of the Scrollbar Kingdom...
 */
class WFakeScrollBar(
    private val scrollFunction: (Int) -> Unit
) : WVerticalSlider() {

    override fun draw() {
        if (isHidden) return

        // why
        val x = x.toDouble()
        val y = y.toDouble()
        val z = z.toDouble()
        val h = height.toDouble()

        if (max == 0f) {
            BaseRenderer.drawImage((x + 1), (y + 1), (z + 2), 12.0, 15.0, guiTex("knob_off"))
            return
        }

        val knobY = y + 1 + ((h - 17) / (max) * (max - progress))

        BaseRenderer.drawImage(
            (x + 1), knobY, (z + 2), 12.0, 15.0,
            guiTex("knob")
        )
    }

    override fun updatePosition(mouseX: Float, mouseY: Float) {
        super.updatePosition(mouseX, mouseY)
        scrollFunction.invoke((max - progress).toInt())
    }

}
