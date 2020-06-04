package io.gitlab.intended.storagenetworks.block.entity

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object ModBlockEntities {

    val MASTER = c(ModBlocks.MASTER) { MasterBlockEntity() }
    val REQUEST = c(ModBlocks.REQUEST) { RequestBlockEntity() }

    val CABLE = c(ModBlocks.CABLE) { CableBlockEntity() }
    val LINK_CABLE = c(ModBlocks.LINK_CABLE) { StorageCableBlockEntity() }

    fun init() {
        r(ModBlocks.MASTER, MASTER)
        r(ModBlocks.REQUEST, REQUEST)

        r(ModBlocks.CABLE, CABLE)
        r(ModBlocks.LINK_CABLE, LINK_CABLE)
    }

    private fun r(modBlock: ModBlock, blockEntityType: BlockEntityType<out BlockEntity>) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, modBlock.id, blockEntityType)
    }

    private fun <T : BlockEntity> c(block: Block, function: () -> T): BlockEntityType<T> {
        return BlockEntityType.Builder.create(Supplier(function), block).build(null)
    }

}
