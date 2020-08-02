package badasintended.slotlink.common.registry

import badasintended.slotlink.client.gui.screen.*
import badasintended.slotlink.common.registry.ScreenHandlerRegistry.REMOTE
import badasintended.slotlink.common.registry.ScreenHandlerRegistry.REQUEST
import badasintended.slotlink.common.registry.ScreenHandlerRegistry.TRANSFER
import badasintended.slotlink.gui.screen.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object ScreenRegistry {

    fun init() {
        r(REQUEST) { i: RequestScreenHandler, _, _ -> RequestScreen(i) }
        r(REMOTE) { i: RemoteScreenHandler, _, _ -> RemoteScreen(i) }
        r(TRANSFER) { i: TransferScreenHandler, _, _ -> TransferScreen(i) }
    }

    private fun <H : ModScreenHandler, S : ModScreen<H>> r(
        type: ScreenHandlerType<H>, function: (H, PlayerInventory, Text) -> S
    ) {
        ScreenRegistry.register(type, ScreenRegistry.Factory(function))
    }

}
