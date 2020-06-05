package io.gitlab.intended.storagenetworks.block

import com.google.common.collect.ImmutableSet
import io.gitlab.intended.storagenetworks.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

object ModBlocks {

    val MASTER = MasterBlock("master")
    val REQUEST = RequestBlock("request")

    val CABLE = CableBlock("cable")
    val LINK_CABLE = LinkCableBlock("link_cable")

    val BLOCKS: ImmutableSet<ModBlock> = ImmutableSet.of(MASTER, REQUEST, CABLE, LINK_CABLE)

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
