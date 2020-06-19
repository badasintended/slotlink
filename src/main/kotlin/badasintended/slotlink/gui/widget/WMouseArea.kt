@file:Suppress("MemberVisibilityCanBePrivate")

package badasintended.slotlink.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WAbstractWidget

@Environment(EnvType.CLIENT)
class WMouseArea : WAbstractWidget() {

    var onMouseClicked: (mouseButton: Int) -> Unit = {}
    var onMouseReleased: (mouseButton: Int) -> Unit = {}
    var onMouseScrolled: (deltaY: Double) -> Unit = {}
    var onMouseMoved: (isInside: Boolean) -> Unit = {}

    override fun onMouseClicked(mouseX: Float, mouseY: Float, mouseButton: Int) {
        super.onMouseClicked(mouseX, mouseY, mouseButton)
        if (isWithinBounds(mouseX, mouseY)) onMouseClicked.invoke(mouseButton)
    }

    override fun onMouseReleased(mouseX: Float, mouseY: Float, mouseButton: Int) {
        super.onMouseReleased(mouseX, mouseY, mouseButton)
        if (isWithinBounds(mouseX, mouseY)) onMouseReleased.invoke(mouseButton)
    }

    override fun onMouseDragged(mouseX: Float, mouseY: Float, mouseButton: Int, deltaX: Double, deltaY: Double) {
        super.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY)
        if (isWithinBounds(mouseX, mouseY)) onMouseReleased.invoke(mouseButton)
    }

    override fun onMouseMoved(mouseX: Float, mouseY: Float) {
        super.onMouseMoved(mouseX, mouseY)
        onMouseMoved.invoke(isWithinBounds(mouseX, mouseY))
    }

    override fun onMouseScrolled(mouseX: Float, mouseY: Float, deltaY: Double) {
        super.onMouseScrolled(mouseX, mouseY, deltaY)
        if (isWithinBounds(mouseX, mouseY)) onMouseScrolled.invoke(deltaY)
    }

}
