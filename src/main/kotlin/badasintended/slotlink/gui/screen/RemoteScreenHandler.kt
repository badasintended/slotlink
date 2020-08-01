package badasintended.slotlink.gui.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf

class RemoteScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : RequestScreenHandler(
    syncId, player, buf
) {

    val offHand = buf.readBoolean()
}
