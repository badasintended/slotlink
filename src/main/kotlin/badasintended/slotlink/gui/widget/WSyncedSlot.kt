package badasintended.slotlink.gui.widget

import net.minecraft.item.ItemStack
import sbinnery.widget.WSlot

class WSyncedSlot(onSetStack: () -> Unit) : WServerSlot(onSetStack) {

    private var syncedStack = ItemStack.EMPTY

    override fun getStack(): ItemStack = syncedStack

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        syncedStack = stack
        onSetStack.invoke()
        return this as W
    }

}
