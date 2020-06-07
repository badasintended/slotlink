package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.Mod
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

abstract class ModBlock(id: String, settings: Settings = SETTINGS) : Block(settings) {

    companion object {
        val SETTINGS: Settings = FabricBlockSettings
            .of(Material.STONE)
            .breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .hardness(5f)
    }

    val id: Identifier = Mod.id(id)

    override fun buildTooltip(stack: ItemStack, view: BlockView?, tooltip: MutableList<Text>, options: TooltipContext) {
        tooltip.add(TranslatableText("${translationKey}.tooltip"))
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)
        world.chunkManager.setChunkForced(ChunkPos(pos), true)
    }

    override fun onBroken(world: IWorld, pos: BlockPos, state: BlockState) {
        super.onBroken(world, pos, state)
        world.chunkManager.setChunkForced(ChunkPos(pos), false)
    }

}
