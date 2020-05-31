package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.item.ModItem
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

object ModBlocks {

    val MASTER = MasterBlock("master")
    val CRAFTING_TERMINAL = CraftingTerminalBlock("crafting_terminal")

    val CABLE = CableBlock("cable")
    val STORAGE_CABLE = StorageCableBlock("storage_cable")

    fun init() {
        register(MASTER, CRAFTING_TERMINAL, CABLE, STORAGE_CABLE)
    }

    private fun register(vararg modBlocks: ModBlock) {
        for (block in modBlocks) {
            Registry.register(Registry.BLOCK, block.id, block)
            Registry.register(Registry.ITEM, block.id, BlockItem(block, ModItem.settings))
        }
    }

}