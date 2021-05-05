package badasintended.slotlink.init

import badasintended.slotlink.client.gui.screen.LinkScreen
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.screen.TransferScreen
import badasintended.slotlink.screen.LinkScreenHandler
import badasintended.slotlink.screen.RemoteScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.screen.TransferScreenHandler
import badasintended.slotlink.util.modId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text

private typealias S<T> = ScreenHandlerType<T>

object Screens : Initializer {

    lateinit var REQUEST: S<RequestScreenHandler>
    lateinit var REMOTE: S<RemoteScreenHandler>
    lateinit var LINK: S<LinkScreenHandler>
    lateinit var TRANSFER: S<TransferScreenHandler>

    override fun main() {
        REQUEST = h("request", ::RequestScreenHandler)
        REMOTE = h("remote", ::RemoteScreenHandler)
        LINK = h("link", ::LinkScreenHandler)
        TRANSFER = h("transfer", ::TransferScreenHandler)
    }

    @Environment(EnvType.CLIENT)
    override fun client() {
        s(REQUEST, ::RequestScreen)
        s(REMOTE, ::RequestScreen)
        s(LINK, ::LinkScreen)
        s(TRANSFER, ::TransferScreen)
    }

    private fun <H : ScreenHandler> h(id: String, func: (Int, PlayerInventory, PacketByteBuf) -> H): S<H> {
        return ScreenHandlerRegistry.registerExtended(modId(id), func)
    }

    private fun <H : ScreenHandler> h(id: String, func: (Int, PlayerInventory) -> H): S<H> {
        return ScreenHandlerRegistry.registerSimple(modId(id), func)
    }

    @Environment(EnvType.CLIENT)
    private fun <H : ScreenHandler, S : HandledScreen<H>> s(type: ScreenHandlerType<H>, function: (H, PlayerInventory, Text) -> S) {
        ScreenRegistry.register(type, ScreenRegistry.Factory(function))
    }

}
