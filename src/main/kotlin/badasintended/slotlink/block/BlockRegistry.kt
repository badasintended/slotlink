package badasintended.slotlink.block

import badasintended.slotlink.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.BLOCK
import net.minecraft.util.registry.Registry.ITEM

object BlockRegistry {

    val MASTER = MasterBlock()
    val REQUEST = RequestBlock()

    val CABLE = CableBlock()
    val LINK_CABLE = LinkCableBlock()

    fun init() {
        r(MASTER, REQUEST, CABLE, LINK_CABLE)
    }

    private fun r(vararg modBlocks: ModBlock) {
        for (block in modBlocks) {
            Registry.register(BLOCK, block.id, block)
            Registry.register(ITEM, block.id, BlockItem(block, ModItem.SETTINGS))
        }
    }

}
