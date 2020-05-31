package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.block.entity.StorageCableBlockEntity
import net.minecraft.world.BlockView

class StorageCableBlock(id: String) : ConnectorCableBlock(id) {

    override fun createBlockEntity(view: BlockView) = StorageCableBlockEntity()

}