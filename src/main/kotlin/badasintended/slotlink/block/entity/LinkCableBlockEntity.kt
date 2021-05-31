package badasintended.slotlink.block.entity

import badasintended.slotlink.api.Compat
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.init.BlockEntityTypes
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
    ConnectorCableBlockEntity(BlockEntityTypes.LINK_CABLE, pos, state) {

    override fun Block.isIgnored(): Boolean {
        if (this is ModBlock || Compat.isBlacklisted(this)) return true
        return ignoredTag.contains(this)
    }

    override fun markDirty() {
        super.markDirty()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos)

            if (master is MasterBlockEntity) {
                master.linkPos.add(pos)
                master.markDirty()
            }
        }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return LinkScreenHandler(syncId, inv, priority, isBlackList, filter, ScreenHandlerContext.create(world, pos))
    }

}
