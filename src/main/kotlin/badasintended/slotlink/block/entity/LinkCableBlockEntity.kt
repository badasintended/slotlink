package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.screen.LinkScreenHandler
import badasintended.slotlink.util.ignoredTag
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.math.BlockPos

class LinkCableBlockEntity(pos: BlockPos, state: BlockState) :
    ConnectorCableBlockEntity(BlockEntityTypes.LINK_CABLE, ConnectionType.LINK, pos, state) {

    override fun Block.isIgnored(): Boolean {
        return this is ModBlock || ignoredTag.contains(this)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return LinkScreenHandler(syncId, inv, priority, isBlackList, filter, ScreenHandlerContext.create(world, pos))
    }

}
