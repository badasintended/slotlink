package io.gitlab.intended.storagenetworks.container

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

object ModContainers {

    fun init() {
        r(ModBlocks.REQUEST) { id, _, player, _ -> RequestContainer(id, player) }
        r(ModBlocks.MASTER) { id, _, player, _ -> MasterContainer(id, player) }
    }

    private fun <C : Container> r(modBlock: ModBlock, function: (Int, Identifier, PlayerEntity, PacketByteBuf) -> C) {
        ContainerProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerFactory(function))
    }

}
