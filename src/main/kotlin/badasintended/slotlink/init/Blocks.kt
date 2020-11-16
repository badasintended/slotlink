package badasintended.slotlink.init

import badasintended.slotlink.block.CableBlock
import badasintended.slotlink.block.ExportCableBlock
import badasintended.slotlink.block.ImportCableBlock
import badasintended.slotlink.block.LinkCableBlock
import badasintended.slotlink.block.MasterBlock
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.RequestBlock
import badasintended.slotlink.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

object Blocks : Initializer {

    val MASTER = MasterBlock()
    val REQUEST = RequestBlock()

    val CABLE = CableBlock()
    val LINK_CABLE = LinkCableBlock()
    val IMPORT_CABLE = ImportCableBlock()
    val EXPORT_CABLE = ExportCableBlock()

    override fun main() {
        r(MASTER, REQUEST, CABLE, LINK_CABLE, IMPORT_CABLE, EXPORT_CABLE)
    }

    private fun r(vararg modBlocks: ModBlock) {
        for (block in modBlocks) {
            Registry.register(Registry.BLOCK, block.id, block)
            Registry.register(Registry.ITEM, block.id, BlockItem(block, ModItem.SETTINGS))
        }
    }

}
