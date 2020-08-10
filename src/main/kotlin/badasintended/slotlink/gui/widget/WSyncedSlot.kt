package badasintended.slotlink.gui.widget

import net.minecraft.item.ItemStack
import spinnery.widget.WSlot

class WSyncedSlot(onSetStack: (Int, Int, ItemStack) -> Unit) : WServerSlot(onSetStack) {

    private var syncedStack = ItemStack.EMPTY

    override fun getStack(): ItemStack = syncedStack

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        syncedStack = stack
        onSetStack.invoke(inventoryNumber, slotNumber, stack)
        return this as W
    }

}
