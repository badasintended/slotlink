package io.gitlab.intended.storagenetworks

import com.google.common.collect.ImmutableMap
import io.gitlab.intended.storagenetworks.block.ModBlock
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import java.util.function.Consumer

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
 * @return a list containing [BlockPos] around parameter
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
 * Apparently, [NbtHelper.fromBlockPos] and [NbtHelper.toBlockPos]
 * use uppercase XYZ instead of lowercase xyz and that drive me nuts so I made this functions instead.
 *
 * FIXME: fix this kinda ocd.
 * @see tag2Pos
 */
fun pos2Tag(pos: BlockPos): CompoundTag {
    val tag = CompoundTag()
    tag.putInt("x", pos.x)
    tag.putInt("y", pos.y)
    tag.putInt("z", pos.z)
    return tag
}

/**
 * @see pos2Tag
 */
fun tag2Pos(tag: CompoundTag): BlockPos {
    return BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"))
}

/**
 * Opens a container, what else?
 */
fun openContainer(block: ModBlock, player: PlayerEntity, function: (PacketByteBuf) -> Unit) {
    ContainerProviderRegistry.INSTANCE.openContainer(block.id, player as ServerPlayerEntity, Consumer(function))
}

/**
 * @return whether a [BlockEntity] has [Inventory] in it.
 */
fun hasInventory(blockEntity: BlockEntity?): Boolean {
    return Inventory::class.java.isAssignableFrom(blockEntity!!.javaClass)
}
