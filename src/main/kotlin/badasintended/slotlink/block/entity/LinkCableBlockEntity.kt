package badasintended.slotlink.block.entity

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.toPos
import badasintended.slotlink.common.util.toTag
import badasintended.slotlink.gui.screen.LinkScreenHandler
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE) {

    override fun WorldAccess.isBlockIgnored(block: Block): Boolean {
        if (block is ModBlock) return true
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
