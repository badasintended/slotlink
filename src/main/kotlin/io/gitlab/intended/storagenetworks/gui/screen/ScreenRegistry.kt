package io.gitlab.intended.storagenetworks.gui.screen

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.gui.container.MasterContainer
import io.gitlab.intended.storagenetworks.gui.container.RequestContainer
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry
import net.minecraft.client.gui.screen.ingame.ContainerScreen
import net.minecraft.container.Container

object ScreenRegistry {

    fun init() {
        r(BlockRegistry.REQUEST) { i: RequestContainer -> RequestScreen(i) }
        r(BlockRegistry.MASTER) { i: MasterContainer -> MasterScreen(i) }
    }

    private fun <C : Container, S : ContainerScreen<C>> r(modBlock: ModBlock, function: (C) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerScreenFactory(function))
    }

}
