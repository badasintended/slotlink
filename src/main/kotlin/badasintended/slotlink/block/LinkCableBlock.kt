package badasintended.slotlink.block

import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.util.ignoredTag
import net.minecraft.block.Block

class LinkCableBlock : ConnectorCableBlock("link_cable", ::LinkCableBlockEntity) {

    override fun isIgnored(block: Block): Boolean {
        return ignoredTag.contains(block)
    }

}
