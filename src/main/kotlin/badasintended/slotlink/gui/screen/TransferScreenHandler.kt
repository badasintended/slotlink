package badasintended.slotlink.gui.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.BlockPos
import spinnery.widget.WSlot

class TransferScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModScreenHandler(syncId, player) {

    val pos: BlockPos = buf.readBlockPos()

    init {
        WSlot.addHeadlessPlayerInventory(root)
    }

}
