package badasintended.slotlink.common.registry

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.*
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE
import java.util.function.Supplier

object BlockEntityTypeRegistry {

    val MASTER = c(BlockRegistry.MASTER) { MasterBlockEntity() }
    val REQUEST = c(BlockRegistry.REQUEST) { RequestBlockEntity() }

    val CABLE = c(BlockRegistry.CABLE) { CableBlockEntity() }
    val LINK_CABLE = c(BlockRegistry.LINK_CABLE) { LinkCableBlockEntity() }
    val IMPORT_CABLE = c(BlockRegistry.IMPORT_CABLE) { ImportCableBlockEntity() }
    val EXPORT_CABLE = c(BlockRegistry.EXPORT_CABLE) { ExportCableBlockEntity() }

    fun init() {
        r(BlockRegistry.MASTER, MASTER)
        r(BlockRegistry.REQUEST, REQUEST)

        r(BlockRegistry.CABLE, CABLE)
        r(BlockRegistry.LINK_CABLE, LINK_CABLE)
        r(BlockRegistry.IMPORT_CABLE, IMPORT_CABLE)
        r(BlockRegistry.EXPORT_CABLE, EXPORT_CABLE)
    }

    private fun r(modBlock: ModBlock, blockEntityType: BlockEntityType<out BlockEntity>) {
        Registry.register(BLOCK_ENTITY_TYPE, modBlock.id, blockEntityType)
    }

    private fun <T : BlockEntity> c(block: Block, function: () -> T): BlockEntityType<T> {
        return BlockEntityType.Builder.create(Supplier(function), block).build(null)
    }

}
