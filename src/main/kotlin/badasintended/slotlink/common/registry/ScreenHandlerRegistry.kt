package badasintended.slotlink.common.registry

import badasintended.slotlink.Slotlink
import badasintended.slotlink.common.util.*
import badasintended.slotlink.gui.screen.*
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.Direction

object ScreenHandlerRegistry {

    lateinit var REQUEST: ScreenHandlerType<RequestScreenHandler>
    lateinit var REMOTE: ScreenHandlerType<RemoteScreenHandler>
    lateinit var TRANSFER: ScreenHandlerType<TransferScreenHandler>

    fun init() {
        REQUEST = rE("request") { id, inv, buf ->
            RequestScreenHandler(
                id, inv, buf.readBlockPos(), buf.readInventorySet(), SortBy.of(buf.readVarInt()),
                ScreenHandlerContext.EMPTY
            )
        }
        REMOTE = rE("remote") { id, inv, buf ->
            RemoteScreenHandler(
                id, inv, buf.readInventorySet(), SortBy.of(buf.readVarInt()), buf.readBoolean(),
                ScreenHandlerContext.EMPTY
            )
        }
        TRANSFER = rE("transfer") { id, inv, buf ->
            TransferScreenHandler(
                id, inv, buf.readBlockPos(), Direction.byId(buf.readVarInt()), buf.readBoolean(), buf.readInventory()
            )
        }
    }

    private fun <H : ModScreenHandler> rE(
        id: String, func: (Int, PlayerInventory, PacketByteBuf) -> H
    ): ScreenHandlerType<H> {
        return ScreenHandlerRegistry.registerExtended(Slotlink.id(id), func)
    }

    private fun <H : ModScreenHandler> rS(id: String, func: (Int, PlayerInventory) -> H): ScreenHandlerType<H> {
        return ScreenHandlerRegistry.registerSimple(Slotlink.id(id), func)
    }

}
