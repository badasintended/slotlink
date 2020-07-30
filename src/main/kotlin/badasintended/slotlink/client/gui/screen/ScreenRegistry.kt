@file:Suppress("DEPRECATION")

package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.Slotlink
import badasintended.slotlink.screen.ModScreenHandler
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry

@Environment(EnvType.CLIENT)
object ScreenRegistry {

    fun init() {
        r("request") { i: RequestScreenHandler -> RequestScreen(i) }
        r("remote") { i: RemoteScreenHandler -> RemoteScreen(i) }
    }

    private fun <H : ModScreenHandler, S : ModScreen<H>> r(id: String, function: (H) -> S) {
        ScreenProviderRegistry.INSTANCE.registerFactory(Slotlink.id(id), ContainerScreenFactory(function))
    }

}
