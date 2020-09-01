package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.gui.screen.RemoteScreenHandler
import badasintended.slotlink.registry.NetworkRegistry.REMOTE_SAVE
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import sbinnery.widget.WSlot

@Environment(EnvType.CLIENT)
class RemoteScreen(c: RemoteScreenHandler) : RequestScreen<RemoteScreenHandler>(c) {

    override fun init() {
        super.init()
        if (!c.offHand) {
            val slot = playerInvSlots.firstOrNull { it.slotNumber == playerInventory.selectedSlot }
            slot?.setHidden<WSlot>(true)
            playerInvSlots.remove(slot)
        }
    }

    override fun saveSort() {
        val buf = buf()
        buf.apply {
            writeBoolean(c.offHand)
            writeInt(lastSort.ordinal)
        }
        c2s(REMOTE_SAVE, buf)
    }

}
