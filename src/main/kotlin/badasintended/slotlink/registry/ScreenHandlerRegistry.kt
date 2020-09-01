package badasintended.slotlink.registry

import badasintended.slotlink.gui.screen.*
import badasintended.slotlink.util.modId
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

typealias S<T> = ScreenHandlerType<T>

object ScreenHandlerRegistry {

    lateinit var REQUEST: S<RequestScreenHandler>
    lateinit var REMOTE: S<RemoteScreenHandler>
    lateinit var LINK: S<LinkScreenHandler>
    lateinit var TRANSFER: S<TransferScreenHandler>

    fun init() {
        REQUEST = r("request", ::RequestScreenHandler)
        REMOTE = r("remote", ::RemoteScreenHandler)
        LINK = r("link", ::LinkScreenHandler)
        TRANSFER = r("transfer", ::TransferScreenHandler)
    }

    private fun <H : ModScreenHandler> r(
        id: String, func: (Int, PlayerInventory, PacketByteBuf) -> H
    ): ScreenHandlerType<H> {
        return ScreenHandlerRegistry.registerExtended(modId(id), func)
    }

}
