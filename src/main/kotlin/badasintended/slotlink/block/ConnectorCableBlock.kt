package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ConnectorCableBlockEntity
import badasintended.slotlink.util.*
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.*
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.*

abstract class ConnectorCableBlock(id: String, be: () -> BlockEntity) : CableBlock(id, be) {

    companion object {
        val end = bbCuboid(5, 5, 5, 6, 6, 6)
    }

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
        if (!neighbor.isIgnored()) {
            if ((world.getBlockEntity(neighborPos) is Inventory) or (neighbor is InventoryProvider)) {
                val blockEntity = world.getBlockEntity(pos) as? ConnectorCableBlockEntity ?: return state
                if ((blockEntity.getInventory(world).isNull) or (blockEntity.linkedPos.toPos() == neighborPos)) {
                    blockEntity.linkedPos = neighborPos.toTag()
                    blockEntity.markDirty()
                    return state.with(propertyMap[facing], true)
                }
            }
        }
        return state
    }

    protected abstract fun Block.isIgnored(): Boolean

    @Suppress("DEPRECATION")
    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
        val result = super.getOutlineShape(state, view, pos, ctx)
        return VoxelShapes.union(result, end)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val state = super.getPlacementState(ctx)
        val world = ctx.world
        val face = ctx.side.opposite
        val pos = ctx.blockPos.offset(face)
        val block = world.getBlockState(pos).block
        return if (!block.isIgnored() and ((world.getBlockEntity(pos) is Inventory) or (block is InventoryProvider))) {
            state?.with(propertyMap[face], true)
        } else {
            state
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        var updatedState = state
        propertyMap.entries.sortedByDescending { state[it.value] }.forEach {
            updatedState = checkLink(world, pos, it.key, updatedState, pos.offset(it.key))
        }
        world.setBlockState(pos, updatedState)
    }

    @Suppress("DEPRECATION")
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
            if (neighbor.isIgnored() and ((world.getBlockEntity(
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

    override fun appendTooltip(
        stack: ItemStack, world: BlockView?, tooltip: MutableList<Text>, options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(TranslatableText("block.slotlink.cable.tooltipFilter").formatted(Formatting.GRAY))
    }

    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        if (player.mainHandStack.isEmpty) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun createScreenHandlerFactory(
        state: BlockState, world: World, pos: BlockPos
    ): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos) ?: return null
        if (blockEntity !is ConnectorCableBlockEntity) return null
        return blockEntity
    }

}
