package badasintended.slotlink.screen

import badasintended.slotlink.Mod
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

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
        ContainerProviderRegistry.INSTANCE.registerFactory(Mod.id(id), ContainerFactory(function))
    }

}
