package badasintended.slotlink.screen

import badasintended.slotlink.Slotlink
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object ScreenHandlerRegistry {

    fun init() {
        r("master") { id, _, player, buf -> MasterScreenHandler(id, player, buf) }
        r("request") { id, _, player, buf -> RequestScreenHandler(id, player, buf) }
        r("remote") { id, _, player, buf -> RemoteScreenHandler(id, player, buf) }
    }

    private fun <H : ModScreenHandler> r(
        id: String,
        function: (Int, Identifier, PlayerEntity, PacketByteBuf) -> H
    ) {
        ContainerProviderRegistry.INSTANCE.registerFactory(Slotlink.id(id), ContainerFactory(function))
    }

}
