package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.spinneryId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.TranslatableText
import spinnery.client.render.BaseRenderer
import spinnery.common.registry.ThemeRegistry
import spinnery.widget.WTextField
import spinnery.widget.api.Style
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WSearchBar(
    private val setSearch: (String) -> Unit,
    private val drawTooltip: () -> Unit
) : WTextField() {

    init {
        setLabel<WSearchBar>(TranslatableText("block.slotlink.request.search"))
    }

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        if (isHidden) return

        val x = floor(x)
        val y = floor(y)
        val w = floor(width)
        val h = floor(height)

        val slotStyle = Style.of(ThemeRegistry.getStyle(theme, spinneryId("slot")))

        BaseRenderer.drawBeveledPanel(
            matrices, provider,
            x, (y + 1), z, (w - 2), (h - 4),
            slotStyle.asColor("top_left"),
            slotStyle.asColor("background.unfocused"),
            slotStyle.asColor("bottom_right")
        )

        if (isFocused and !isActive) drawTooltip.invoke()

        renderField(matrices, provider)
    }

    override fun onKeyReleased(keyCode: Int, character: Int, keyModifier: Int) {
        super.onKeyReleased(keyCode, character, keyModifier)
        setSearch.invoke(text)
    }

    override fun onMouseClicked(mouseX: Float, mouseY: Float, mouseButton: Int) {
        active = isFocused
    }

}
