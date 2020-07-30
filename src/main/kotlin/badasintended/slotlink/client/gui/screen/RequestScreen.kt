package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.common.buf
import badasintended.slotlink.network.NetworkRegistry.REMOTE_SAVE
import badasintended.slotlink.network.NetworkRegistry.REQUEST_SAVE
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
import spinnery.widget.WSlot

@Environment(EnvType.CLIENT)
class RequestScreen(c: RequestScreenHandler) : AbstractRequestScreen<RequestScreenHandler>(c) {

    override fun saveSort() {
        val buf = buf()
        buf.writeBlockPos(c.blockPos)
        buf.writeInt(lastSort.ordinal)
        INSTANCE.sendToServer(REQUEST_SAVE, buf)
    }

}

@Environment(EnvType.CLIENT)
class RemoteScreen(c: RemoteScreenHandler) : AbstractRequestScreen<RemoteScreenHandler>(c) {

    override fun init() {
        super.init()
        if (!c.offHand) {
            val slot = playerInvSlots.firstOrNull() { it.slotNumber == playerInventory.selectedSlot }
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
