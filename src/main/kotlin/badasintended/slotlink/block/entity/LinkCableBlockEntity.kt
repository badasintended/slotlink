package badasintended.slotlink.block.entity

import badasintended.slotlink.Slotlink
import badasintended.slotlink.api.Compat
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.gui.screen.LinkScreenHandler
import badasintended.slotlink.registry.BlockEntityTypeRegistry
import badasintended.slotlink.util.toPos
import badasintended.slotlink.util.toTag
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE) {

    override fun WorldAccess.isBlockIgnored(block: Block): Boolean {
        if ((block is ModBlock) or Compat.isBlacklisted(block)) return true
        return if (this is World) tagManager.blocks.getTag(Slotlink.id("ignored"))?.contains(block) ?: false else false
    }

    override fun markDirty() {
        super.markDirty()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos.toPos())

            if (master is MasterBlockEntity) {
                master.linkCables.add(pos.toTag())
                master.markDirty()
            }
        }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        return LinkScreenHandler(syncId, inv, pos, priority, isBlackList, filter)
    }

}
