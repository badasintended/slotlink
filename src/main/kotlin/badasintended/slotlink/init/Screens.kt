package badasintended.slotlink.init

import badasintended.slotlink.client.gui.screen.ConnectorCableScreen
import badasintended.slotlink.client.gui.screen.FilterScreen
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.screen.TransferCableScreen
import badasintended.slotlink.screen.ConnectorCableScreenHandler
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.screen.TransferCableScreenHandler
import badasintended.slotlink.util.modId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.registry.Registry
import net.minecraft.client.gui.screen.ingame.HandledScreens.register as r

object Screens : Initializer {

    val REQUEST = ScreenHandlerType(::RequestScreenHandler)
    val REMOTE = ExtendedScreenHandlerType(::RemoteScreenHandler)
    val FILTER = ExtendedScreenHandlerType(::FilterScreenHandler)
    val CONNECTOR_CABLE = ExtendedScreenHandlerType(::ConnectorCableScreenHandler)
    val TRANSFER_CABLE = ExtendedScreenHandlerType(::TransferCableScreenHandler)

    override fun main() {
        r("request", REQUEST)
        r("remote", REMOTE)
        r("filter", FILTER)
        r("connector_cable", CONNECTOR_CABLE)
        r("transfer_cable", TRANSFER_CABLE)
    }

    @Environment(EnvType.CLIENT)
    override fun client() {
        r(REQUEST, ::RequestScreen)
        r(REMOTE, ::RequestScreen)
        r(FILTER, ::FilterScreen)
        r(CONNECTOR_CABLE, ::ConnectorCableScreen)
        r(TRANSFER_CABLE, ::TransferCableScreen)
    }

    private fun <H : ScreenHandler> r(id: String, type: ScreenHandlerType<H>) {
        Registry.register(Registry.SCREEN_HANDLER, modId(id), type)
    }

}
