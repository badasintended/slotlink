package io.gitlab.intended.storagenetworks.gui.widget

import net.minecraft.item.ItemStack
import spinnery.widget.WSlot
import spinnery.widget.api.Action

class WCraftingOutputSlot(
    private val craftingSlots: HashSet<WCraftingInputSlot>
) : WSlot() {

    private fun clearInput() {
        craftingSlots.forEach { it.setStack<WSlot>(ItemStack(it.stack.item, (it.stack.count - 1))) }
    }

    override fun consume(action: Action, subtype: Action.Subtype) {
        if (action == Action.PICKUP) {
            clearInput()
        } else if (action == Action.QUICK_MOVE) {
            clearInput()
        }
        super.consume(action, subtype)
    }

}
