package badasintended.slotlink.common.registry

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE
import java.util.function.Supplier

object BlockEntityTypeRegistry {

    lateinit var MASTER: BlockEntityType<*>
    lateinit var REQUEST: BlockEntityType<*>

    lateinit var CABLE: BlockEntityType<*>
    lateinit var LINK_CABLE: BlockEntityType<*>
    lateinit var IMPORT_CABLE: BlockEntityType<*>
    lateinit var EXPORT_CABLE: BlockEntityType<*>

    fun init() {
        MASTER = r(BlockRegistry.MASTER, ::MasterBlockEntity)
        REQUEST = r(BlockRegistry.REQUEST, ::RequestBlockEntity)

        CABLE = r(BlockRegistry.CABLE, ::CableBlockEntity)
        LINK_CABLE = r(BlockRegistry.LINK_CABLE, ::LinkCableBlockEntity)
        IMPORT_CABLE = r(BlockRegistry.IMPORT_CABLE, ::ImportCableBlockEntity)
        EXPORT_CABLE = r(BlockRegistry.EXPORT_CABLE, ::ExportCableBlockEntity)
    }

    private fun <T : BlockEntity> r(block: ModBlock, function: () -> T): BlockEntityType<T> {
        return Registry.register(
            BLOCK_ENTITY_TYPE, block.id, BlockEntityType.Builder.create(Supplier(function), block).build(null)
        )
    }

}
