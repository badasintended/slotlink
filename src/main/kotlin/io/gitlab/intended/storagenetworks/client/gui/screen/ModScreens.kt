package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.inventory.CraftingTerminalInventory
import io.gitlab.intended.storagenetworks.inventory.MasterInventory
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.container.Container

object ModScreens {

    fun init() {
        reg(ModBlocks.CRAFTING_TERMINAL) { i: CraftingTerminalInventory -> CraftingTerminalScreen(i) }
        reg(ModBlocks.MASTER) { i: MasterInventory -> MasterScreen(i) }
    }

    private fun <C : Container, S : ContainerScreen<C>> reg(modBlock: ModBlock, function: (C) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerScreenFactory(function))
    }

}