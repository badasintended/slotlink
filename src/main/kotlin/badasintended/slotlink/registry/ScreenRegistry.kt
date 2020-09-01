package badasintended.slotlink.registry

import badasintended.slotlink.client.gui.screen.*
import badasintended.slotlink.gui.screen.ModScreenHandler
import badasintended.slotlink.registry.ScreenHandlerRegistry.LINK
import badasintended.slotlink.registry.ScreenHandlerRegistry.REMOTE
import badasintended.slotlink.registry.ScreenHandlerRegistry.REQUEST
import badasintended.slotlink.registry.ScreenHandlerRegistry.TRANSFER
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object ScreenRegistry {

    fun init() {
        r(REQUEST) { i, _, _ -> RequestScreen(i) }
        r(REMOTE) { i, _, _ -> RemoteScreen(i) }
        r(LINK) { i, _, _ -> LinkScreen(i) }
        r(TRANSFER) { i, _, _ -> TransferScreen(i) }
    }

    private fun <H : ModScreenHandler, S : ModScreen<H>> r(
        type: ScreenHandlerType<H>, function: (H, PlayerInventory, Text) -> S
    ) {
        ScreenRegistry.register(type, ScreenRegistry.Factory(function))
    }

}
