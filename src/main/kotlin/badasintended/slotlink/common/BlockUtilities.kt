package badasintended.slotlink.common

import badasintended.slotlink.Mod
import com.google.common.collect.ImmutableMap
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.WorldAccess

/**
 * Generates [VoxelShape] based on the position that shows on [Blockbench](https://blockbench.net).
 * No thinking required!
 */
fun bbCuboid(xPos: Int, yPos: Int, zPos: Int, xSize: Int, ySize: Int, zSize: Int): VoxelShape {
    val xMin = xPos / 16.0
    val yMin = yPos / 16.0
    val zMin = zPos / 16.0
    val xMax = (xPos + xSize) / 16.0
    val yMax = (yPos + ySize) / 16.0
    val zMax = (zPos + zSize) / 16.0
    return VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax)
}

/**
 * @return a map containing [Direction]s with corresponding [BlockPos] around parameter
 */
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
 * @param blockEntity must already checked with [Block.hasBlockEntity]
 * @return whether a [BlockEntity] has [Inventory] in it.
 */
fun hasInventory(blockEntity: BlockEntity?): Boolean {
    return Inventory::class.java.isAssignableFrom(blockEntity!!.javaClass)
}

fun isIgnored(block: Block, world: WorldAccess): Boolean {
    return world.world.tagManager.blocks().get(Mod.id("ignored"))!!.contains(block)
}
