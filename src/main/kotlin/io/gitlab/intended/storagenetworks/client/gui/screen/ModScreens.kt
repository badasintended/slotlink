package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.inventory.MasterInventory
import io.gitlab.intended.storagenetworks.inventory.RequestInventory
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.container.Container

object ModScreens {

    fun init() {
        r(ModBlocks.REQUEST) { i: RequestInventory -> RequestScreen(i) }
        r(ModBlocks.MASTER) { i: MasterInventory -> MasterScreen(i) }
    }

    private fun <C : Container, S : ContainerScreen<C>> r(modBlock: ModBlock, function: (C) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerScreenFactory(function))
    }

}