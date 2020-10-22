package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.init.Networks
import badasintended.slotlink.mixin.CraftingScreenHandlerAccessor
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import net.minecraft.client.gui.screen.Screen

class CraftingResultSlotWidget(
    private val handler: RequestScreenHandler,
    x: Int, y: Int
) : SlotWidget(x, y, 26, handler.playerInventory, { (handler as CraftingScreenHandlerAccessor).result.getStack(0) }) {

    override fun onClick(button: Int) {
        val buf = buf().apply {
            writeVarInt(handler.syncId)
            writeVarInt(button)
            writeBoolean(Screen.hasShiftDown())
        }
        c2s(Networks.CRAFTING_RESULT_SLOT_CLICK, buf)
    }

}
