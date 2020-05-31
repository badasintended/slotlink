package io.gitlab.intended.storagenetworks.inventory

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.Container

object ModInventories {

    fun init() {
        register(ModBlocks.CRAFTING_TERMINAL, ContainerFactory { syncId, _, player, buf -> CraftingTerminalInventory(syncId, player, buf.readText()) })
    }

    private fun register(modBlock: ModBlock, factory: ContainerFactory<Container>) = ContainerProviderRegistry.INSTANCE.registerFactory(modBlock.id, factory)

}