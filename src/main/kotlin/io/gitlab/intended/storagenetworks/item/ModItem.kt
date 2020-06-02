package io.gitlab.intended.storagenetworks.item

import io.gitlab.intended.storagenetworks.Mod
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

abstract class ModItem(settings: Settings) : Item(settings) {

    companion object {
        private val GROUP: ItemGroup = FabricItemGroupBuilder.build(Mod.id("group")) { ItemStack(ModBlocks.MASTER) }

        val settings: Settings get() = Settings().group(GROUP)
    }

}