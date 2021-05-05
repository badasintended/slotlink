package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.client
import badasintended.slotlink.util.toFormattedString
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.widget.AbstractButtonWidget
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

@Environment(EnvType.CLIENT)
class SlotCountWidget(
    x: Int, y: Int, backgroundWidth: Int,
    private val total: () -> Int,
    private val filled: () -> Int
) : AbstractButtonWidget(x, y, backgroundWidth, client.textRenderer.fontHeight, LiteralText.EMPTY) {

    private var textX = x

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float) {
        val text = (total.invoke() - filled.invoke()).toFormattedString()
        client.textRenderer.apply {
            textX = x + width - (getWidth(text) + 7)
            draw(matrices, text, textX.toFloat(), y.toFloat(), 0x404040)
        }
    }

    override fun renderToolTip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        if (textX < mouseX && mouseX < x + width - 7) client.currentScreen?.apply {
            renderTooltip(matrices, TranslatableText("container.slotlink.request.slotCount", filled.invoke(), total.invoke()), mouseX, mouseY)
        }
    }

}