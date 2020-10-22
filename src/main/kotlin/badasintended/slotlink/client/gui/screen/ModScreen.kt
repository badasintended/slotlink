package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.util.bindGuiTexture
import badasintended.slotlink.util.drawNinePatch
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
abstract class ModScreen<H : ScreenHandler>(h: H, inventory: PlayerInventory, title: Text) : HandledScreen<H>(h, inventory, title) {

    abstract val baseTlKey: String

    fun tl(key: String, vararg args: Any) = TranslatableText("$baseTlKey.$key", *args)

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(matrices, mouseX, mouseY, delta)

        if (playerInventory.cursorStack.isEmpty && focusedSlot != null && focusedSlot!!.hasStack()) {
            this.renderTooltip(matrices, focusedSlot!!.stack, mouseX, mouseY)
        } else {
            val hovered = hoveredElement(mouseX.toDouble(), mouseY.toDouble())
            if (hovered.isPresent) {
                (hovered.get() as? AbstractButtonWidget)?.renderToolTip(matrices, mouseX, mouseY)
            }
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

}
