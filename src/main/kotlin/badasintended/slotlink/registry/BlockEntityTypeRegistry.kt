package badasintended.slotlink.registry

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.BLOCK_ENTITY_TYPE
import java.util.function.Supplier

typealias T = BlockEntityType<*>
typealias B = BlockRegistry

object BlockEntityTypeRegistry {

    lateinit var MASTER: T
    lateinit var REQUEST: T

    lateinit var CABLE: T
    lateinit var LINK_CABLE: T
    lateinit var IMPORT_CABLE: T
    lateinit var EXPORT_CABLE: T

    fun init() {
        MASTER = r(B.MASTER, ::MasterBlockEntity)
        REQUEST = r(B.REQUEST, ::RequestBlockEntity)

        CABLE = r(B.CABLE, ::CableBlockEntity)
        LINK_CABLE = r(B.LINK_CABLE, ::LinkCableBlockEntity)
        IMPORT_CABLE = r(B.IMPORT_CABLE, ::ImportCableBlockEntity)
        EXPORT_CABLE = r(B.EXPORT_CABLE, ::ExportCableBlockEntity)
    }

    private fun <E : BlockEntity> r(block: ModBlock, function: () -> E): BlockEntityType<E> {
        return Registry.register(
            BLOCK_ENTITY_TYPE, block.id, BlockEntityType.Builder.create(Supplier(function), block).build(null)
        )
    }

}
