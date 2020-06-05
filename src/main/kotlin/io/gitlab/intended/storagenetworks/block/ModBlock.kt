package io.gitlab.intended.storagenetworks.block

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import io.gitlab.intended.storagenetworks.Mod
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
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

        /**
         * Generates [VoxelShape] based on the position that shows on [Blockbench](https://blockbench.net).
         * No thinking required!
         */
        fun cuboid(xPos: Int, yPos: Int, zPos: Int, xSize: Int, ySize: Int, zSize: Int): VoxelShape {
            val xMin = xPos / 16.0
            val yMin = yPos / 16.0
            val zMin = zPos / 16.0
            val xMax = (xPos + xSize) / 16.0
            val yMax = (yPos + ySize) / 16.0
            val zMax = (zPos + zSize) / 16.0
            return VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax)
        }

        /**
         * @return a list containing [BlockPos] around parameter
         */
        fun posAround(pos: BlockPos): ImmutableList<BlockPos> {
            return ImmutableList.of(pos.north(), pos.south(), pos.west(), pos.east(), pos.up(), pos.down())
        }

        fun posFacingAround(pos: BlockPos): ImmutableMap<Direction, BlockPos> {
            return ImmutableMap.builder<Direction, BlockPos>()
                .put(Direction.NORTH, pos.north())
                .put(Direction.SOUTH, pos.south())
                .put(Direction.EAST, pos.east())
                .put(Direction.WEST, pos.west())
                .put(Direction.UP, pos.up())
                .put(Direction.DOWN, pos.down())
                .build()
        }

        /**
         * Apparently, [net.minecraft.nbt.NbtHelper.fromBlockPos] and [net.minecraft.nbt.NbtHelper.toBlockPos]
         * use capital XYZ instead of lowercase xyz and that drive me nuts so I made this functions instead.
         *
         * FIXME: fix this kinda ocd.
         */
        fun pos2Tag(pos: BlockPos): CompoundTag {
            val tag = CompoundTag()
            tag.putInt("x", pos.x)
            tag.putInt("y", pos.y)
            tag.putInt("z", pos.z)
            return tag
        }

        fun tag2Pos(tag: CompoundTag): BlockPos {
            return BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"))
        }

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
