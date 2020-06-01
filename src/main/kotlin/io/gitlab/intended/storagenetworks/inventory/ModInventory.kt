package io.gitlab.intended.storagenetworks.inventory

import io.gitlab.intended.storagenetworks.block.ModBlock
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.world.World
import spinnery.common.BaseContainer

abstract class ModInventory(
    syncId: Int,
    val player: PlayerEntity,
    val name: Text
) : BaseContainer(syncId, player.inventory) {

    companion object {
        fun open(world: World, modBlock: ModBlock, player: PlayerEntity): ActionResult {
            return if (!world.isClient) {
                player as ServerPlayerEntity
                ContainerProviderRegistry.INSTANCE.openContainer(modBlock.id, player) { buf ->
                    buf.writeText(TranslatableText(modBlock.translationKey))
                }
                ActionResult.SUCCESS
            } else {
                ActionResult.PASS
            }
        }
    }

}