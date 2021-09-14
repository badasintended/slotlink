package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.client
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
abstract class SlotWidget<SH : ScreenHandler>(
    x: Int, y: Int, s: Int,
    protected val handler: SH,
    private val stackGetter: () -> ItemStack
) : NoSoundWidget(x, y, s, s, LiteralText.EMPTY) {

    val stack get() = stackGetter.invoke()

    private val stackX = x - 8 + width / 2
    private val stackY = y - 8 + height / 2

    abstract fun onClick(button: Int)

    protected open fun appendTooltip(tooltip: MutableList<Text>) {}

    protected open fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        client.apply {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, stackX, stackY)
        }
    }

    final override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        client.itemRenderer.renderGuiItemIcon(stack, stackX, stackY)
        renderOverlay(matrices, stack)
    }

    final override fun renderTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        matrices.push()
        matrices.translate(0.0, 0.0, +256.0)
        val x = stackX
        val y = stackY
        fill(matrices, x, y, x + 16, y + 16, -2130706433 /*0x80ffffff fuck*/)

        client.apply {
            if (handler.cursorStack.isEmpty && !stack.isEmpty) {
                val tooltips = stack.getTooltip(player) { options.advancedItemTooltips }
                appendTooltip(tooltips)
                currentScreen?.renderTooltip(matrices, tooltips, mouseX, mouseY)
            }
        }
        matrices.pop()
    }

    final override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (hovered && visible) {
            onClick(button)
            return true
        }
        return false
    }

}
