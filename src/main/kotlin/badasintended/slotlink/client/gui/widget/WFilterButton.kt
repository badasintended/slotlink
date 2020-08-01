package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.Slotlink
import net.minecraft.text.TranslatableText
import spinnery.widget.WAbstractButton
import kotlin.reflect.KMutableProperty0

class WFilterButton(
    private val isBlackList: KMutableProperty0<Boolean>, private val save: () -> Any
) : WSlotButton() {

    override fun getTextureId() = Slotlink.id("textures/gui/${if (isBlackList.get()) "black" else "white"}list.png")

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) {
            isBlackList.set(!isBlackList.get())
            save.invoke()
        }

        return super.setLowered(toggleState)
    }

    override fun getTooltip() = listOf(
        TranslatableText("container.slotlink.transfer.${if (isBlackList.get()) "black" else "white"}list")
    )

}
