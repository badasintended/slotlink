package io.gitlab.intended.storagenetworks.inventory

import io.gitlab.intended.storagenetworks.block.ModBlock
import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

object ModInventories {

    fun init() {
        reg(ModBlocks.CRAFTING_TERMINAL) { id, _, player, buf -> CraftingTerminalInventory(id, player, buf.readText()) }
        reg(ModBlocks.MASTER) { id, _, player, buf -> MasterInventory(id, player, buf.readText()) }
    }

    private fun <C : Container> reg(modBlock: ModBlock, function: (Int, Identifier, PlayerEntity, PacketByteBuf) -> C) {
        ContainerProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerFactory(function))
    }

}