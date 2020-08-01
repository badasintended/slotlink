package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.util.next
import badasintended.slotlink.common.util.texture
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Direction
import spinnery.widget.WAbstractButton
import kotlin.reflect.KMutableProperty0

class WSideButton(
    private val direction: KMutableProperty0<Direction>, private val save: () -> Any
) : WSlotButton() {

    override fun getTextureId() = direction.get().texture()

    override fun <W : WAbstractButton> setLowered(toggleState: Boolean): W {
        if (toggleState) {
            direction.set(direction.get().next())
            save.invoke()
        }
        return super.setLowered(toggleState)
    }

    override fun getTooltip() = listOf(
        TranslatableText("container.slotlink.transfer.side.${direction.get().asString()}")
    )

}
