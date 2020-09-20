package badasintended.slotlink.gui.widget

import net.minecraft.item.ItemStack
import sbinnery.widget.WSlot

open class WServerSlot(
    protected val onSetStack: () -> Unit
) : WSlot() {

    @Suppress("UNCHECKED_CAST")
    override fun <W : WSlot> setStack(stack: ItemStack): W {
        super.setStack<W>(stack)
        linkedInventory.markDirty()
        onSetStack.invoke()
        return this as W
    }

}
