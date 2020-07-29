package badasintended.slotlink.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf

class RequestScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    AbstractRequestScreenHandler(syncId, player, buf)

class RemoteScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    AbstractRequestScreenHandler(syncId, player, buf) {

    val offHand = buf.readBoolean()


}
