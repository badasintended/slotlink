package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.compat.invsort.InventorySortButton
import badasintended.slotlink.client.gui.widget.CharGrabber
import badasintended.slotlink.client.gui.widget.KeyGrabber
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
abstract class ModScreen<H : ScreenHandler>(h: H, inventory: PlayerInventory, title: Text) :
    HandledScreen<H>(h, inventory, title) {

    abstract val baseTlKey: String

    private var clickedElement: ClickableWidget? = null
    var hoveredElement: ClickableWidget? = null

    fun tl(key: String, vararg args: Any) = Text.translatable("$baseTlKey.$key", *args)!!

    protected inline fun <T : ClickableWidget> add(t: T, func: T.() -> Unit = {}): T {
        return addDrawableChild(t).apply(func)
    }

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        if (handler.cursorStack.isEmpty && focusedSlot != null && focusedSlot!!.hasStack()) {
            this.renderTooltip(matrices, focusedSlot!!.stack, mouseX, mouseY)
        } else {
            hoveredElement = hoveredElement(mouseX.toDouble(), mouseY.toDouble()).orElse(null) as? ClickableWidget
            if (hoveredElement !is InventorySortButton) hoveredElement?.renderTooltip(matrices, mouseX, mouseY)
        }
    }

    override fun drawBackground(matrices: MatrixStack, delta: Float, mouseX: Int, mouseY: Int) {
        renderBackground(matrices)
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

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        return children().any { it is KeyGrabber && it.onKey(keyCode, scanCode, modifiers) }
            || super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun charTyped(char: Char, modifiers: Int): Boolean {
        return children().any { it is CharGrabber && it.onChar(char, modifiers) }
            || super.charTyped(char, modifiers)
    }

}
