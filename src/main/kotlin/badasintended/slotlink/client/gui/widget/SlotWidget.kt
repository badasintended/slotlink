package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.client
import badasintended.slotlink.client.util.wrap
import badasintended.slotlink.compat.recipe.RecipeViewer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.item.TooltipContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
abstract class SlotWidget<SH : ScreenHandler>(
    x: Int, y: Int, s: Int,
    protected val handler: SH,
    private val stackGetter: () -> ItemStack
) : NoSoundWidget(x, y, s, s, ScreenTexts.EMPTY),
    TooltipRenderer {

    val stack get() = stackGetter.invoke()

    private val stackX = x - 8 + width / 2
    private val stackY = y - 8 + height / 2

    abstract fun onClick(button: Int)

    protected open fun appendTooltip(tooltip: MutableList<Text>) {}

    protected open fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        client.apply {
            itemRenderer.renderGuiItemOverlay(matrices, textRenderer, stack, stackX, stackY)
        }
    }

    override fun renderTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        matrices.wrap {
            matrices.translate(0.0, 0.0, +256.0)
            val x = stackX
            val y = stackY
            fill(matrices, x, y, x + 16, y + 16, -2130706433 /*0x80ffffff fuck*/)
            if (!stack.isEmpty && handler.cursorStack.isEmpty && RecipeViewer.instance?.isDraggingStack != true)
                client.apply {
                    val tooltips = stack.getTooltip(
                        player,
                        TooltipContext.Default(options.advancedItemTooltips, player?.isCreative ?: false)
                    )
                    appendTooltip(tooltips)
                    currentScreen?.renderTooltip(matrices, tooltips, mouseX, mouseY)
                }
        }
    }

    final override fun renderButton(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        if (!visible) return
        client.itemRenderer.renderGuiItemIcon(matrices, stack, stackX, stackY)
        renderOverlay(matrices, stack)
    }

    final override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val player = client.player ?: return false
        if (hovered && visible && !player.isSpectator) {
            onClick(button)
            return true
        }
        return false
    }

    override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {}

}
