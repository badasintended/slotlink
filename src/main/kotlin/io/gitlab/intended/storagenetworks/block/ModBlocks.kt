package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

object ModBlocks {

    val MASTER = MasterBlock("master")
    val REQUEST = RequestBlock("request")

    val CABLE = CableBlock("cable")
    val LINK_CABLE = StorageCableBlock("link_cable")

    fun init() {
        r(MASTER, REQUEST, CABLE, LINK_CABLE)
    }

    private fun r(vararg modBlocks: ModBlock) {
        for (block in modBlocks) {
            Registry.register(Registry.BLOCK, block.id, block)
            Registry.register(Registry.ITEM, block.id, BlockItem(block, ModItem.settings))
        }
    }

}