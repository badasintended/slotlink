package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.SortBy
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import spinnery.widget.WAbstractButton

@Environment(EnvType.CLIENT)
class WSortButton(
    private var sort: SortBy, private val sortFunction: () -> SortBy
) : WSlotButton() {

    private val tooltip = arrayListOf<Text>(
        TranslatableText(sort.translationKey).formatted(Formatting.GRAY)
    )

    override fun getTextureId() = sort.texture

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) {
            sort = sortFunction.invoke()
            tooltip[0] = TranslatableText(sort.translationKey).formatted(Formatting.GRAY)
        }
        return super.setLowered(toggleState)
    }

    override fun getTooltip() = tooltip

}
