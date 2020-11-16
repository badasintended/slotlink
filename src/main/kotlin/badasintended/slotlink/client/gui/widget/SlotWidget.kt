package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.util.bindGuiTexture
import badasintended.slotlink.util.drawNinePatch
import badasintended.slotlink.util.getClient
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

abstract class SlotWidget(
    x: Int, y: Int, s: Int,
    private val playerInventory: PlayerInventory,
    private val stackGetter: () -> ItemStack
) : AbstractButtonWidget(x, y, s, s, LiteralText.EMPTY) {

    private val stackX = x - 8 + width / 2
    private val stackY = y - 8 + height / 2

    abstract fun onClick(button: Int)

    protected open fun appendTooltip(tooltip: MutableList<Text>) {}

    protected open fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        getClient().apply {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, stackX, stackY)
        }
    }

    final override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        bindGuiTexture()

        drawNinePatch(matrices, x, y, width, height, 16f, 0f, 1, 14)

        val stack = stackGetter.invoke()

        getClient().itemRenderer.renderGuiItemIcon(stack, stackX, stackY)
        renderOverlay(matrices, stack)
    }

    final override fun renderToolTip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        matrices.push()
        matrices.translate(0.0, 0.0, +256.0)
        val x = stackX
        val y = stackY
        fill(matrices, x, y, x + 16, y + 16, -2130706433 /*0x80ffffff fuck*/)

        val stack = stackGetter.invoke()

        getClient().apply {
            if (playerInventory.cursorStack.isEmpty and !stack.isEmpty) {
                val tooltips = stack.getTooltip(player) { options.advancedItemTooltips }
                appendTooltip(tooltips)
                currentScreen?.renderTooltip(matrices, tooltips, mouseX, mouseY)
            }
        }
        matrices.pop()
    }

    final override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (hovered and visible) {
            onClick(button)
            return true
        }
        return false
    }

}
