@file:Suppress("DEPRECATION")

package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.*
import io.gitlab.intended.storagenetworks.block.entity.LinkCableBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityContext
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ConnectorCableBlock(
    id: String,
    private val blockEntity: KClass<out BlockEntity>
) : CableBlock(id) {

    /**
     * Checks linked block (e.g. chest) and update block state accordingly.
     * If the current linked block doesn't have [Inventory], update it.
     * Otherwise, keep it and don't "connect (visually)" to checked block.
     *
     * Dominant direction (from most to least):
     * NORTH, SOUTH, EAST, WEST, UP, DOWN.
     * (Based on [CableBlock.propertyMap])
     *
     * It will connect to the most dominant first, and remains connected
     * even when more dominant is placed after.
     * Example: when cable already connected to WEST, it will remains
     * connected to WEST even when we place SOUTH later on.
     *
     * TODO: Optimize, maybe.
     */
    private fun checkLink(
        world: IWorld,
        pos: BlockPos,
        facing: Direction,
        state: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        val neighbor = world.getBlockState(neighborPos).block
        if (neighbor.hasBlockEntity()) {
            if (hasInventory(world.getBlockEntity(neighborPos))) {
                val blockEntity = world.getBlockEntity(pos)!!
                val nbt = blockEntity.toTag(CompoundTag())
                val linkedPosTag = nbt.getCompound("linkedPos")

                var changeLink = false

                if (linkedPosTag == CompoundTag()) {
                    changeLink = true
                } else {
                    val linkedPos = tag2Pos(linkedPosTag)
                    val linked = world.getBlockState(linkedPos).block
                    if (!linked.hasBlockEntity()) {
                        changeLink = true
                    } else if (!hasInventory(world.getBlockEntity(linkedPos))) {
                        changeLink = true
                    } else if (neighborPos == linkedPos) {
                        changeLink = true
                    }
                }

                if (changeLink) {
                    nbt.put("linkedPos", pos2Tag(neighborPos))
                    blockEntity.fromTag(nbt)
                    blockEntity.markDirty()
                    return state.with(propertyMap[facing], true)
                }
            }
        }
        return state
    }

    override fun createBlockEntity(view: BlockView) = blockEntity.createInstance()

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: EntityContext): VoxelShape {
        val end = bbCuboid(5, 5, 5, 6, 6, 6)
        val result = super.getOutlineShape(state, view, pos, ctx)
        return VoxelShapes.union(result, end)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        var updatedState = state

        posFacingAround(pos).forEach { (facing, neighborPos) ->
            updatedState = checkLink(world, pos, facing, updatedState, neighborPos)
        }

        world.setBlockState(pos, updatedState)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        facing: Direction,
        neighborState: BlockState,
        world: IWorld,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val fromSuper = super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos)
        var updatedState = checkLink(world, pos, facing, fromSuper, neighborPos)
        if (neighborPos == tag2Pos(world.getBlockEntity(pos)!!.toTag(CompoundTag()).getCompound("linkedPos"))) {
            var checkAround = false
            if (neighborState.block.hasBlockEntity()) {
                if (!hasInventory(world.getBlockEntity(neighborPos))) {
                    checkAround = true
                }
            } else checkAround = true
            if (checkAround) {
                posFacingAround(pos).forEach { (facingAround, posAround) ->
                    updatedState = checkLink(world, pos, facingAround, updatedState, posAround)
                }
            }
        }
        return updatedState
    }

}

class LinkCableBlock(id: String) : ConnectorCableBlock(id, LinkCableBlockEntity::class)
