package badasintended.slotlink.init

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.util.BlockEntityBuilder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry
import badasintended.slotlink.init.Blocks as B

private typealias T = BlockEntityType<*>

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

    private fun r(block: ModBlock, function: BlockEntityBuilder): T {
        return Registry.register(
            Registry.BLOCK_ENTITY_TYPE, block.id, BlockEntityType.Builder.create(function, block).build(null)
        )
    }

}
