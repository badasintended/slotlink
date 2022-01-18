@file:Suppress("UnstableApiUsage")

package badasintended.slotlink.init

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.block.entity.ExportCableBlockEntity
import badasintended.slotlink.block.entity.ImportCableBlockEntity
import badasintended.slotlink.block.entity.InterfaceBlockEntity
import badasintended.slotlink.block.entity.LinkCableBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.block.entity.RequestBlockEntity
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import badasintended.slotlink.init.Blocks as B
import net.minecraft.block.entity.BlockEntityType as T

object BlockEntityTypes : Initializer {

    lateinit var MASTER: T<MasterBlockEntity>
    lateinit var REQUEST: T<RequestBlockEntity>
    lateinit var INTERFACE: T<InterfaceBlockEntity>

    lateinit var CABLE: T<CableBlockEntity>
    lateinit var LINK_CABLE: T<LinkCableBlockEntity>
    lateinit var IMPORT_CABLE: T<ImportCableBlockEntity>
    lateinit var EXPORT_CABLE: T<ExportCableBlockEntity>

    override fun main() {
        MASTER = r(B.MASTER, ::MasterBlockEntity)
        REQUEST = r(B.REQUEST, ::RequestBlockEntity)
        INTERFACE = r(B.INTERFACE, ::InterfaceBlockEntity)

        CABLE = r(B.CABLE, ::CableBlockEntity)
        LINK_CABLE = r(B.LINK_CABLE, ::LinkCableBlockEntity)
        IMPORT_CABLE = r(B.IMPORT_CABLE, ::ImportCableBlockEntity)
        EXPORT_CABLE = r(B.EXPORT_CABLE, ::ExportCableBlockEntity)

        ItemStorage.SIDED.registerForBlockEntity(InterfaceBlockEntity::getStorage, INTERFACE)
    }

    private fun <BE : BlockEntity> r(block: ModBlock, function: (BlockPos, BlockState) -> BE): T<BE> {
        return Registry.register(
            Registry.BLOCK_ENTITY_TYPE, block.id, T.Builder.create(function, block).build(null)
        )
    }

}
