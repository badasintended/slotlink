package badasintended.slotlink.block

import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.util.ignoredTag
import net.minecraft.block.BlockState

class LinkCableBlock : ConnectorCableBlock("link_cable", ::LinkCableBlockEntity) {

    override fun isIgnored(blockState: BlockState): Boolean {
        return blockState.isIn(ignoredTag)
    }

}
