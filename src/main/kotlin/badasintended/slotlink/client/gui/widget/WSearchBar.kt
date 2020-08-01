package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WTextField
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSearchBar(
    private val search: (String) -> Unit
) : WTextField() {

    companion object {
        val EMPTY = arrayListOf<Text>()
    }

    private val tooltip = arrayListOf<Text>(
        TranslatableText("block.slotlink.request.search.tooltip1").formatted(Formatting.GRAY),
        TranslatableText("block.slotlink.request.search.tooltip2").formatted(Formatting.GRAY)
    )

    init {
        setLabel<WSearchBar>(TranslatableText("block.slotlink.request.search"))
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider) {
        if (isHidden) return

        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(
            ThemeRegistry.getStyle(
                theme, spinneryId("slot")
            )
        )

        BaseRenderer.drawBeveledPanel(
            matrices, provider, x, (y + 1), z, (w - 2), (h - 4), slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"), slotStyle.asColor("bottom_right")
        )

        renderField(matrices, provider)
    }

    override fun onKeyReleased(keyCode: Int, character: Int, keyModifier: Int) {
        super.onKeyReleased(keyCode, character, keyModifier)
        search.invoke(text)
    }

    override fun onMouseClicked(mouseX: Float, mouseY: Float, mouseButton: Int) {
        active = isWithinBounds(mouseX, mouseY)
        if (active and (mouseButton == 1)) {
            setText<WSearchBar>("")
            cursor.assign(Cursor(0, 0))
            search.invoke(text)
        }
    }

    override fun getTooltip() = if (active) EMPTY else tooltip

}
