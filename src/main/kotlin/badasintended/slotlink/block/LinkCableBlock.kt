package badasintended.slotlink.block

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import net.minecraft.block.Block
import net.minecraft.world.WorldAccess

class LinkCableBlock : ConnectorCableBlock("link_cable", LinkCableBlockEntity::class) {

    override fun WorldAccess.isBlockIgnored(block: Block): Boolean {
        if (block is ModBlock) return true
        return world.tagManager.blocks().get(Slotlink.id("ignored"))?.contains(block) ?: false
    }

}
