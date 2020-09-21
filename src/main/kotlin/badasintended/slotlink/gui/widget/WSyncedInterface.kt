package badasintended.slotlink.gui.widget

import badasintended.slotlink.gui.screen.RequestScreenHandler
import sbinnery.widget.WInterface
import sbinnery.widget.WSlot

class WSyncedInterface(
    handler: RequestScreenHandler, private val onSetStack: () -> Unit
) : WInterface(handler) {

    @Suppress("UNCHECKED_CAST")
    override fun <W : WSlot> getSlot(inventoryNumber: Int, slotNumber: Int): W {
        var slot = allWidgets
            .filterIsInstance<WSlot>()
            .firstOrNull { (it.inventoryNumber == inventoryNumber) and (it.slotNumber == slotNumber) }

        if (slot == null) {
            slot = createChild { WSyncedSlot(onSetStack) }
            slot.setNumber<WSlot>(inventoryNumber, slotNumber)
            (handler as RequestScreenHandler).linkedSlots.add(slot)
        }

        return slot as W
    }

}
