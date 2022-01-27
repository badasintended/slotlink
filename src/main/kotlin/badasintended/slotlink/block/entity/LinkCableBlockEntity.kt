package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.screen.ConnectorCableScreenHandler
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.math.BlockPos

class LinkCableBlockEntity(pos: BlockPos, state: BlockState) :
    ConnectorCableBlockEntity(Blocks.LINK_CABLE, BlockEntityTypes.LINK_CABLE, NodeType.LINK, pos, state) {

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity) = ConnectorCableScreenHandler(
        syncId, inv, blacklist, filter, priority, ScreenHandlerContext.create(world, pos)
    )

}
