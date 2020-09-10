package badasintended.slotlink.block.entity

import badasintended.slotlink.api.Compat
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.gui.screen.LinkScreenHandler
import badasintended.slotlink.registry.BlockEntityTypeRegistry
import badasintended.slotlink.util.*
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE) {

    override fun Block.isIgnored(): Boolean {
        if ((this is ModBlock) or Compat.isBlacklisted(this)) return true
        return TagRegistry.block(modId("ignored")).contains(this)
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
