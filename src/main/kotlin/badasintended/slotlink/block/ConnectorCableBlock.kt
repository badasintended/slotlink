package badasintended.slotlink.block

import badasintended.slotlink.property.NullableProperty
import badasintended.slotlink.property.getNull
import badasintended.slotlink.property.with
import badasintended.slotlink.util.BlockEntityBuilder
import badasintended.slotlink.util.bbCuboid
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
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

abstract class ConnectorCableBlock(id: String, builder: BlockEntityBuilder) : CableBlock(id, builder) {

    companion object {

        val CONNECTED = NullableProperty(DirectionProperty.of("connected"))
        val end = bbCuboid(5, 5, 5, 6, 6, 6)

    }

    init {
        defaultState = defaultState.with(CONNECTED, null)
    }

    @Suppress("DEPRECATION", "UnstableApiUsage")
    private fun checkLink(
        state: BlockState,
        direction: Direction,
        world: WorldAccess,
        neighborState: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        if (world !is ServerWorld) return state

        var result = state
        val connected = state.getNull(CONNECTED)
        val block = neighborState.block
        if (connected == direction && neighborState.isAir) {
            result = result.with(CONNECTED, null)
        } else if (connected == null && !block.isIgnored()) {
            for (d in DIRECTIONS) {
                val storage = ItemStorage.SIDED.find(world, neighborPos, d)
                if (storage != null) {
                    result = result
                        .with(CONNECTED, direction)
                        .with(PROPERTIES[direction], true)
                    break
                }
            }
        }
        return result
    }

    protected abstract fun Block.isIgnored(): Boolean

    override fun center() = end

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

    override fun connect(
        state: BlockState,
        direction: Direction,
        world: WorldAccess,
        neighborState: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        val fromSuper = super.connect(state, direction, world, neighborState, neighborPos)
        return checkLink(fromSuper, direction, world, neighborState, neighborPos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        var state = super.getPlacementState(ctx) ?: return null
        val connected = state.getNull(CONNECTED)
        state = state.with(CONNECTED, null)
        if (connected != null) {
            state = state.with(PROPERTIES[connected], false)
        }

        val world = ctx.world
        val opposite = ctx.side.opposite
        val oppositePos = ctx.blockPos.offset(opposite)
        val oppositeState = world.getBlockState(oppositePos)

        state = checkLink(state, opposite, world, oppositeState, oppositePos)
        if (state.getNull(CONNECTED) == null) {
            state = state.with(CONNECTED, connected)
            if (connected != null) {
                state = state.with(PROPERTIES[connected], true)
            }
        }

        return state
    }

    @Suppress("DEPRECATION")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        var updatedState = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
        if (updatedState.getNull(CONNECTED) == null) {
            DIRECTIONS.forEach {
                val offset = pos.offset(it)
                updatedState = connect(updatedState, it, world, world.getBlockState(offset), offset)
            }
        }
        updatedState.getNull(CONNECTED)?.also {
            updatedState = updatedState.with(PROPERTIES[it], true)
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
        tooltip.add(TranslatableText("block.slotlink.filter.tooltip").formatted(Formatting.GRAY))
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

}
