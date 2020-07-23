package badasintended.slotlink.client.gui.widget

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import spinnery.client.render.AdvancedItemRenderer
import spinnery.client.render.BaseRenderer
import spinnery.widget.WSlot
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

/**
 * [WSlot] that explicitly uses vanilla [ItemRenderer] instead of [AdvancedItemRenderer],
 * therefore make it compatible to baked models using fabric api.
 *
 * TODO: remove when fixed in spinnery
 */
open class WVanillaSlot : WSlot() {

    protected fun countText(value: Int): String {
        return when {
            value <= 1 -> ""
            value < 1000 -> "$value"
            else -> {
                val exp = (ln(value.toDouble()) / ln(1000.0)).toInt()
                String.format("%.1f%c", value / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
            }
        }
    }

    protected open fun drawItem(
        matrices: MatrixStack,
        stack: ItemStack, itemRenderer: ItemRenderer, textRenderer: TextRenderer,
        x: Float, y: Float, w: Float, h: Float
    ) {
        val count = stack.count
        val countText = countText(count)

        val itemX = ((1 + x) + ((w - 18) / 2)).toInt()
        val itemY = ((1 + y) + ((h - 18) / 2)).toInt()

        itemRenderer.renderGuiItemIcon(stack, itemX, itemY)
        itemRenderer.renderGuiItemOverlay(
            textRenderer, stack, itemX, itemY, countText
        )
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider) {
        if (isHidden) return

        provider as VertexConsumerProvider.Immediate

        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        BaseRenderer.drawBeveledPanel(
            matrices, provider, x, y, z, w, h,
            style.asColor("top_left"),
            style.asColor("background.unfocused"),
            style.asColor("bottom_right")
        )
        provider.draw()

        drawItem(
            matrices,
            if (previewStack.isEmpty) stack else previewStack,
            BaseRenderer.getDefaultItemRenderer(),
            BaseRenderer.getDefaultTextRenderer(),
            x, y, w, h
        )

        if (isFocused) BaseRenderer.drawQuad(
            matrices, provider,
            (x + 1), (y + 1), (z + 201), (w - 2), (h - 2),
            style.asColor("overlay")
        )
    }

}
