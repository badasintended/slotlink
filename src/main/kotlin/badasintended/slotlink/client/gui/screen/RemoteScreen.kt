package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.gui.screen.RemoteScreenHandler
import badasintended.slotlink.registry.NetworkRegistry.REMOTE_SAVE
import badasintended.slotlink.util.buf
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
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
        buf.writeBoolean(c.offHand)
        buf.writeInt(lastSort.ordinal)
        INSTANCE.sendToServer(REMOTE_SAVE, buf)
    }

}
