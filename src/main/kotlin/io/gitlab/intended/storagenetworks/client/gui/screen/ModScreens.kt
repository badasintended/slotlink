package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.container.MasterContainer
import io.gitlab.intended.storagenetworks.container.RequestContainer
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.container.Container

object ModScreens {

    fun init() {
        r(ModBlocks.REQUEST) { i: RequestContainer -> RequestScreen(i) }
        r(ModBlocks.MASTER) { i: MasterContainer -> MasterScreen(i) }
    }

    private fun <C : Container, S : ContainerScreen<C>> r(modBlock: ModBlock, function: (C) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerScreenFactory(function))
    }

}
