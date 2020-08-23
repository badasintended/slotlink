package badasintended.slotlink.block

import badasintended.slotlink.Slotlink
import badasintended.slotlink.api.Compat
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class LinkCableBlock : ConnectorCableBlock("link_cable", ::LinkCableBlockEntity) {

    override fun WorldAccess.isBlockIgnored(block: Block): Boolean {
        if ((block is ModBlock) or Compat.isBlacklisted(block)) return true
        return if (this is World) tagManager.blocks.getTag(Slotlink.id("ignored"))?.contains(block) ?: false else false
    }

}
