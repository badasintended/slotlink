package io.gitlab.intended.storagenetworks.gui.widget

import net.minecraft.item.ItemStack
import spinnery.widget.WSlot
import java.util.function.Supplier

class WCraftingInputSlot(
    private val function: () -> Unit
) : WSlot() {

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        val result = super.setStack<W>(stack)
        Supplier(function).get()
        return result
    }

}
