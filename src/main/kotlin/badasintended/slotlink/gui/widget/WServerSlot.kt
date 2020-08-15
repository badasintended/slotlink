package badasintended.slotlink.gui.widget

import net.minecraft.item.ItemStack
import sbinnery.widget.WSlot

open class WServerSlot(
    protected val onSetStack: () -> Unit
) : WSlot() {

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        super.setStack<W>(stack)
        onSetStack.invoke()
        return this as W
    }

}
