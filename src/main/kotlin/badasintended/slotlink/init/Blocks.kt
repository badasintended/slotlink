package badasintended.slotlink.init

import badasintended.slotlink.block.CableBlock
import badasintended.slotlink.block.ExportCableBlock
import badasintended.slotlink.block.ImportCableBlock
import badasintended.slotlink.block.InterfaceBlock
import badasintended.slotlink.block.LinkCableBlock
import badasintended.slotlink.block.MasterBlock
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.RequestBlock
import badasintended.slotlink.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object Blocks : Initializer {

    val BLOCKS = arrayListOf<ModBlock>()

    val MASTER = MasterBlock()
    val REQUEST = RequestBlock()
    val INTERFACE = InterfaceBlock()

    val CABLE = CableBlock()
    val LINK_CABLE = LinkCableBlock()
    val IMPORT_CABLE = ImportCableBlock()
    val EXPORT_CABLE = ExportCableBlock()

    override fun main() {
        r(MASTER, REQUEST, INTERFACE, CABLE, LINK_CABLE, IMPORT_CABLE, EXPORT_CABLE)
    }

    private fun r(vararg modBlocks: ModBlock) {
        modBlocks.forEach {
            Registry.register(Registries.BLOCK, it.id, it)
            Registry.register(Registries.ITEM, it.id, BlockItem(it, ModItem.SETTINGS))
            BLOCKS.add(it)
        }
    }

}
