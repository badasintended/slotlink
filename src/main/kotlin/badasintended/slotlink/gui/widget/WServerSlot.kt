package badasintended.slotlink.gui.widget

import net.minecraft.item.ItemStack
import spinnery.widget.WSlot

class WServerSlot(
    private val onSetStack: () -> Unit
) : WSlot() {

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        super.setStack<W>(stack)
        onSetStack.invoke()
        return this as W
    }

}
