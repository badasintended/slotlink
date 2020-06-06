package io.gitlab.intended.storagenetworks.container

import io.github.cottonmc.cotton.gui.CottonCraftingController
import io.gitlab.intended.storagenetworks.block.ModBlock
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos

abstract class ModContainer(
    syncId: Int,
    val player: PlayerEntity,
    buf: PacketByteBuf,
    val context: BlockContext = BlockContext.create(player.world, buf.readBlockPos())
) : CottonCraftingController(
    RecipeType.CRAFTING,
    syncId,
    player.inventory,
    getBlockInventory(context),
    getBlockPropertyDelegate(context)
) {

    companion object {
        fun open(block: ModBlock, pos: BlockPos, player: PlayerEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(block.id, player as ServerPlayerEntity) {
                it.writeBlockPos(pos)
            }
        }
    }

}
