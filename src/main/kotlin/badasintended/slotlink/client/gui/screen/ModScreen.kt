package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.compat.invsort.InventorySortButton
import badasintended.slotlink.client.util.bindGuiTexture
import badasintended.slotlink.client.util.drawNinePatch
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
abstract class ModScreen<H : ScreenHandler>(h: H, inventory: PlayerInventory, title: Text) :
    HandledScreen<H>(h, inventory, title) {

    abstract val baseTlKey: String

    private var clickedElement: AbstractButtonWidget? = null
    var hoveredElement: AbstractButtonWidget? = null

    fun tl(key: String, vararg args: Any) = TranslatableText("$baseTlKey.$key", *args)

    protected inline fun <T : AbstractButtonWidget> add(t: T, func: T.() -> Unit = {}): T {
        return addButton(t).apply(func)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        if (playerInventory.cursorStack.isEmpty && focusedSlot != null && focusedSlot!!.hasStack()) {
            this.renderTooltip(matrices, focusedSlot!!.stack, mouseX, mouseY)
        } else {
            hoveredElement = hoveredElement(mouseX.toDouble(), mouseY.toDouble()).orElse(null) as? AbstractButtonWidget
            if (hoveredElement !is InventorySortButton) hoveredElement?.renderToolTip(matrices, mouseX, mouseY)
        }
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        renderBackground(matrices)
        bindGuiTexture()

        drawNinePatch(matrices, x, y, backgroundWidth, backgroundHeight, 0f, 0f, 4, 8)

        handler.slots.forEach {
            if (it != null) {
                drawNinePatch(matrices, x + it.x - 1, y + it.y - 1, 18, 18, 16f, 0f, 1, 14)
            }
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val result = super.mouseClicked(mouseX, mouseY, button)
        clickedElement = hoveredElement
        return result
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        isDragging = false
        val result = clickedElement?.mouseReleased(mouseX, mouseY, button) ?: false
        clickedElement = null
        return result || super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        focused?.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

}
