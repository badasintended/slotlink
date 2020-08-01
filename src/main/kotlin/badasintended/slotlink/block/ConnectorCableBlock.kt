package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ConnectorCableBlockEntity
import badasintended.slotlink.common.util.*
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class ConnectorCableBlock(
    id: String, private val blockEntity: KClass<out BlockEntity>
) : CableBlock(id) {

    /**
     * - Checks linked block (e.g. chest) and update block state accordingly.
     *   If the current linked block doesn't have [Inventory], update it.
     *   Otherwise, keep it and ignore other block.
     *
     * - Dominant direction (from most to least):
     *   NORTH, SOUTH, EAST, WEST, UP, DOWN.
     *   (Based on [CableBlock.propertyMap].)
     *
     * - It will connect to the most dominant direction first, and remains connected
     *   even when more dominant direction is placed after.
     *   **Example**: when cable already connected to WEST, it will remains
     *   connected to WEST even when we place on SOUTH later on.
     *
     * TODO: Optimize, maybe.
     */
    private fun checkLink(
        world: WorldAccess, pos: BlockPos, facing: Direction, state: BlockState, neighborPos: BlockPos
    ): BlockState {
        val neighbor = world.getBlockState(neighborPos).block
        if (!world.isBlockIgnored(neighbor)) {
            if ((world.getBlockEntity(neighborPos) is Inventory) or (neighbor is InventoryProvider)) {
                val blockEntity = world.getBlockEntity(pos) as ConnectorCableBlockEntity
                if ((blockEntity.getLinkedInventory(world) == null) or (blockEntity.linkedPos.toPos() == neighborPos)) {
                    blockEntity.linkedPos = neighborPos.toTag()
                    blockEntity.markDirty()
                    return state.with(propertyMap[facing], true)
                }
            }
        }
        return state
    }

    protected abstract fun WorldAccess.isBlockIgnored(block: Block): Boolean

    override fun createBlockEntity(view: BlockView) = blockEntity.createInstance()

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
        val end = bbCuboid(5, 5, 5, 6, 6, 6)
        val result = super.getOutlineShape(state, view, pos, ctx)
        return VoxelShapes.union(result, end)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        var updatedState = state

        pos.around().forEach { (facing, neighborPos) ->
            updatedState = checkLink(world, pos, facing, updatedState, neighborPos)
        }

        world.setBlockState(pos, updatedState)
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        facing: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        val fromSuper = super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos)
        var updatedState = checkLink(world, pos, facing, fromSuper, neighborPos)
        if (neighborPos == (world.getBlockEntity(pos) as ConnectorCableBlockEntity).linkedPos.toPos()) {
            val neighbor = neighborState.block
            if (world.isBlockIgnored(neighbor) and ((world.getBlockEntity(
                    neighborPos
                ) !is Inventory) and (neighbor !is InventoryProvider))
            ) {
                pos.around().forEach { (facingAround, posAround) ->
                    updatedState = checkLink(world, pos, facingAround, updatedState, posAround)
                }
            }
        }
        return updatedState
    }

}
