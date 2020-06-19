package badasintended.slotlink.gui.container

import badasintended.slotlink.block.ModBlock
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.PacketByteBuf
import spinnery.common.container.BaseContainer
import spinnery.widget.WInterface

abstract class ModContainer(
    val block: ModBlock,
    syncId: Int,
    val player: PlayerEntity,
    buf: PacketByteBuf
) : BaseContainer(syncId, player.inventory) {

    val context: BlockContext = BlockContext.create(player.world, buf.readBlockPos())

    protected val root: WInterface = `interface`

}
