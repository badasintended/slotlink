package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.inventory.CraftingTerminalInventory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.container.Container
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory as Factory

object ModScreens {

    fun init() {
        register(ModBlocks.CRAFTING_TERMINAL, Factory<CraftingTerminalInventory> { c -> CraftingTerminalScreen(c) })
    }

    private fun <C : Container> register(modBlock: ModBlock, factory: Factory<C>) = ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, factory)

}