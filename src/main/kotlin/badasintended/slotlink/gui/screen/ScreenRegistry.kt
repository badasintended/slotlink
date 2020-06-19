package badasintended.slotlink.gui.screen

import badasintended.slotlink.block.BlockRegistry
import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.gui.container.MasterContainer
import badasintended.slotlink.gui.container.ModContainer
import badasintended.slotlink.gui.container.RequestContainer
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry

object ScreenRegistry {

    fun init() {
        r(BlockRegistry.REQUEST) { i: RequestContainer -> RequestScreen(i) }
        r(BlockRegistry.MASTER) { i: MasterContainer -> MasterScreen(i) }
    }

    private fun <C : ModContainer, S : ModScreen<C>> r(modBlock: ModBlock, function: (C) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerScreenFactory(function))
    }

}
