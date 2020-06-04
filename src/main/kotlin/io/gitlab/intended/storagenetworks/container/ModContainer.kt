package io.gitlab.intended.storagenetworks.container

import io.gitlab.intended.storagenetworks.block.ModBlock
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import spinnery.common.BaseContainer

abstract class ModContainer(
    syncId: Int,
    val player: PlayerEntity
) : BaseContainer(syncId, player.inventory) {

    companion object {
        fun open(block: ModBlock, player: PlayerEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(block.id, player as ServerPlayerEntity) {
                it.writeText(TranslatableText(block.translationKey))
            }
        }
    }

}
