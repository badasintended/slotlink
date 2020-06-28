package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.network.NetworkRegistry.REMOTE_SAVE
import badasintended.slotlink.network.NetworkRegistry.REQUEST_SAVE
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
import net.minecraft.network.PacketByteBuf

@Environment(EnvType.CLIENT)
class RequestScreen(c: RequestScreenHandler) : AbstractRequestScreen<RequestScreenHandler>(c) {

    override fun saveSort() {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeBlockPos(c.blockPos)
        buf.writeInt(lastSort.ordinal)
        INSTANCE.sendToServer(REQUEST_SAVE, buf)
    }

}

@Environment(EnvType.CLIENT)
class RemoteScreen(c: RemoteScreenHandler) : AbstractRequestScreen<RemoteScreenHandler>(c) {

    override fun saveSort() {
        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeBoolean(c.offHand)
        buf.writeInt(lastSort.ordinal)
        INSTANCE.sendToServer(REMOTE_SAVE, buf)
    }

}
