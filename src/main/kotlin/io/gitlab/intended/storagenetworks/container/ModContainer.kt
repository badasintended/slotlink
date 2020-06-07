package io.gitlab.intended.storagenetworks.container

import io.github.cottonmc.cotton.gui.CottonCraftingController
import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.recipe.RecipeType
import net.minecraft.util.PacketByteBuf

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
)
