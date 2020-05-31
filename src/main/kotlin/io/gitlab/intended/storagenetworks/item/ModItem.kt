package io.gitlab.intended.storagenetworks.item

import io.gitlab.intended.storagenetworks.StorageNetworks
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

abstract class ModItem(settings: Settings) : Item(settings) {

    companion object {
        private val GROUP: ItemGroup = FabricItemGroupBuilder.build(StorageNetworks.id("gang")) { ItemStack(ModBlocks.MASTER) }

        val settings: Settings get() = Settings().group(GROUP)
    }

}