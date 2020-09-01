package badasintended.slotlink.gui.widget

import badasintended.slotlink.gui.screen.RequestScreenHandler
import net.minecraft.item.ItemStack
import sbinnery.widget.WSlot

class WCraftingResultSlot(private val handler: RequestScreenHandler, onSetStack: () -> Unit) : WServerSlot(onSetStack) {

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        if (!stack.isEmpty) stack.onCraft(handler.world, handler.player, stack.count - getStack().count)
        handler.resultInv.unlockLastRecipe(handler.player)
        return super.setStack(stack)
    }

}
