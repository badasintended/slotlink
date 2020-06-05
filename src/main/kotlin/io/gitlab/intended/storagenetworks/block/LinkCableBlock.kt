package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.block.entity.LinkCableBlockEntity
import net.minecraft.world.BlockView

class LinkCableBlock(id: String) : ConnectorCableBlock(id) {

    override fun createBlockEntity(view: BlockView) = LinkCableBlockEntity()

}
