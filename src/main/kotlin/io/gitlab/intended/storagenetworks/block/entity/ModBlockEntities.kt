package io.gitlab.intended.storagenetworks.block.entity

import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object ModBlockEntities {

    val MASTER = create(Supplier { MasterBlockEntity() }, ModBlocks.MASTER)
    val CRAFTING_TERMINAL = create(Supplier { CraftingTerminalBlockEntity() }, ModBlocks.CRAFTING_TERMINAL)

    val CABLE = create(Supplier { CableBlockEntity() }, ModBlocks.CABLE)
    val STORAGE_CABLE = create(Supplier { StorageCableBlockEntity() }, ModBlocks.STORAGE_CABLE)

    fun init() {
        register(ModBlocks.MASTER, MASTER)
        register(ModBlocks.CRAFTING_TERMINAL, CRAFTING_TERMINAL)

        register(ModBlocks.CABLE, CABLE)
    }

    private fun register(
        modBlock: io.gitlab.intended.storagenetworks.block.ModBlock,
        blockEntityType: BlockEntityType<out BlockEntity>
    ) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, modBlock.id, blockEntityType)
    }

    private fun <T : BlockEntity> create(supplier: Supplier<T>, block: Block): BlockEntityType<T> {
        return BlockEntityType.Builder.create(supplier, block).build(null)
    }

}