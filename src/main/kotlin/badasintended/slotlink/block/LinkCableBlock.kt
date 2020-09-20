package badasintended.slotlink.block

import badasintended.slotlink.api.Compat
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.util.ignoredTag
import net.minecraft.block.Block

class LinkCableBlock : ConnectorCableBlock("link_cable", ::LinkCableBlockEntity) {

    override fun Block.isIgnored(): Boolean {
        if ((this is ModBlock) or Compat.isBlacklisted(this)) return true
        return ignoredTag.contains(this)
    }

}
