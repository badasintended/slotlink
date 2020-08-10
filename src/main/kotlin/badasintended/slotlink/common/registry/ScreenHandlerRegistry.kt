package badasintended.slotlink.common.registry

import badasintended.slotlink.Slotlink
import badasintended.slotlink.gui.screen.*
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

object ScreenHandlerRegistry {

    lateinit var REQUEST: ScreenHandlerType<RequestScreenHandler>
    lateinit var REMOTE: ScreenHandlerType<RemoteScreenHandler>
    lateinit var LINK: ScreenHandlerType<LinkScreenHandler>
    lateinit var TRANSFER: ScreenHandlerType<TransferScreenHandler>

    fun init() {
        REQUEST = r("request", ::RequestScreenHandler)
        REMOTE = r("remote", ::RemoteScreenHandler)
        LINK = r("link", ::LinkScreenHandler)
        TRANSFER = r("transfer", ::TransferScreenHandler)
    }

    private fun <H : ModScreenHandler> r(
        id: String, func: (Int, PlayerInventory, PacketByteBuf) -> H
    ): ScreenHandlerType<H> {
        return ScreenHandlerRegistry.registerExtended(Slotlink.id(id), func)
    }

}
