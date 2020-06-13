package io.gitlab.intended.storagenetworks.gui.widget

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.item.ItemStack
import spinnery.widget.WSlot

class WInventorySlot(
    private val sortFunction: () -> Unit
) : WSlot() {

    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        super.onMouseReleased(mouseX, mouseY, button)
        if (isWithinBounds(mouseX, mouseY)) GlobalScope.launch {
            delay(250)
            sortFunction.invoke()
        }
    }

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        val result = super.setStack<W>(stack)
        sortFunction.invoke()
        return result
    }

}
