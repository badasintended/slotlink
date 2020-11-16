package badasintended.slotlink.init

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import java.util.function.Supplier
import net.minecraft.block.entity.BlockEntity
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
        MASTER = r(Blocks.MASTER, ::MasterBlockEntity)
        REQUEST = r(Blocks.REQUEST, ::RequestBlockEntity)

        CABLE = r(Blocks.CABLE, ::CableBlockEntity)
        LINK_CABLE = r(Blocks.LINK_CABLE, ::LinkCableBlockEntity)
        IMPORT_CABLE = r(Blocks.IMPORT_CABLE, ::ImportCableBlockEntity)
        EXPORT_CABLE = r(Blocks.EXPORT_CABLE, ::ExportCableBlockEntity)
    }

    private fun <E : BlockEntity> r(block: ModBlock, function: () -> E): BlockEntityType<E> {
        return Registry.register(
            Registry.BLOCK_ENTITY_TYPE, block.id, BlockEntityType.Builder.create(Supplier(function), block).build(null)
        )
    }

}
