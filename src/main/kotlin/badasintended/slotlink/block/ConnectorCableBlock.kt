package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ConnectorCableBlockEntity
import badasintended.slotlink.util.bbCuboid
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
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
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlock(id: String, be: () -> BlockEntity) : CableBlock(id, be) {

    companion object {

        val end = bbCuboid(5, 5, 5, 6, 6, 6)

    }

    /**
     * TODO: Optimize, maybe.
     */
    private fun checkLink(
        world: WorldAccess,
        pos: BlockPos,
        facing: Direction,
        state: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        val neighbor = world.getBlockState(neighborPos).block
        if (!neighbor.isIgnored()) {
            if ((world.getBlockEntity(neighborPos) is Inventory) || (neighbor is InventoryProvider)) {
                val blockEntity = world.getBlockEntity(pos) as? ConnectorCableBlockEntity ?: return state
                if ((blockEntity.getInventory(world).isNull) || (blockEntity.linkedPos == neighborPos)) {
                    blockEntity.linkedPos = neighborPos
                    blockEntity.markDirty()
                    return state.with(properties[facing], true)
                }
            }
        }
        return state
    }

    protected abstract fun Block.isIgnored(): Boolean

    override fun center() = end

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val state = super.getPlacementState(ctx)
        val world = ctx.world
        val face = ctx.side.opposite
        val pos = ctx.blockPos.offset(face)
        val block = world.getBlockState(pos).block
        return if (!block.isIgnored() && (world.getBlockEntity(pos) is Inventory || block is InventoryProvider)) {
            state?.with(properties[face], true)
        } else {
            state
        }
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        var updatedState = state
        properties.entries.sortedByDescending { state[it.value] }.forEach {
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
        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is ConnectorCableBlockEntity) if (neighborPos == blockEntity.linkedPos) {
            val neighbor = neighborState.block
            if (neighbor.isIgnored() || !((world.getBlockEntity(neighborPos) is Inventory) || (neighbor is InventoryProvider))) {
                properties.keys.forEach {
                    updatedState = checkLink(world, pos, it, updatedState, pos.offset(it))
                }
            }
        }
        return updatedState
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(TranslatableText("block.slotlink.cable.tooltipFilter").formatted(Formatting.GRAY))
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player.mainHandStack.isEmpty) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun createScreenHandlerFactory(
        state: BlockState,
        world: World,
        pos: BlockPos
    ): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos) ?: return null
        if (blockEntity !is ConnectorCableBlockEntity) return null
        return blockEntity
    }

}
