package badasintended.slotlink.block.entity

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.toPos
import badasintended.slotlink.common.util.toTag
import net.minecraft.block.Block
import net.minecraft.world.WorldAccess

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE) {

    override fun WorldAccess.isBlockIgnored(block: Block): Boolean {
        if (block is ModBlock) return true
        return world.tagManager.blocks().get(Slotlink.id("ignored"))?.contains(block) ?: false
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

}
