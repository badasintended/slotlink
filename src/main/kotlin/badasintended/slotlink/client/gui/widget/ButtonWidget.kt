package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.util.bindGuiTexture
import badasintended.slotlink.util.drawNinePatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class ButtonWidget(x: Int, y: Int, w: Int, h: Int, text: Text) : AbstractButtonWidget(x, y, w, h, text) {

    var onHovered: (MatrixStack, Int, Int) -> Unit = { _, _, _ -> }
    var onPressed = { }
    var u = { 0 }
    var v = { 0 }
    var background = true

    private var down = false

    override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        bindGuiTexture()

        if (background) {
            if (down) {
                drawNinePatch(matrices, x, y, width, height, 16f, 0f, 1, 14)
            } else {
                drawNinePatch(matrices, x, y, width, height, 32f, 0f, 1, 14)
            }
        }

        drawTexture(matrices, x, y, u.invoke(), v.invoke(), width, height)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isMouseOver(mouseX, mouseY)) {
            onPressed.invoke()
            down = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        down = false
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun renderToolTip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (visible) onHovered.invoke(matrices, mouseX, mouseY)
    }

}
