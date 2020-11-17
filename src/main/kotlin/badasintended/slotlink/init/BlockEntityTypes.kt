package badasintended.slotlink.init

import java.util.function.Supplier
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.ModBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

typealias T = BlockEntityType<*>
typealias B = Blocks

object BlockEntityTypes : Initializer {

    lateinit var MASTER: T
    lateinit var REQUEST: T

    lateinit var CABLE: T
    lateinit var LINK_CABLE: T
    lateinit var IMPORT_CABLE: T
    lateinit var EXPORT_CABLE: T

    override fun main() {
        MASTER = r(B.MASTER, ::MasterBlockEntity)
        REQUEST = r(B.REQUEST, ::RequestBlockEntity)

        CABLE = r(B.CABLE, ::CableBlockEntity)
        LINK_CABLE = r(B.LINK_CABLE, ::LinkCableBlockEntity)
        IMPORT_CABLE = r(B.IMPORT_CABLE, ::ImportCableBlockEntity)
        EXPORT_CABLE = r(B.EXPORT_CABLE, ::ExportCableBlockEntity)
    }

    private fun <E : ModBlockEntity> r(block: ModBlock, function: () -> E): BlockEntityType<E> {
        return Registry.register(
            Registry.BLOCK_ENTITY_TYPE, block.id, BlockEntityType.Builder.create(Supplier(function), block).build(null)
        )
    }

}
