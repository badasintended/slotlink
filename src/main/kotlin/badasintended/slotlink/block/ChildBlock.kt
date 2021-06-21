package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.network.Connection
import badasintended.slotlink.util.BlockEntityBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ChildBlock(
    id: String,
    private val blockEntityBuilder: BlockEntityBuilder,
    settings: Settings = SETTINGS
) : ModBlock(id, settings) {

    override fun neighborUpdate(
        selfState: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        if (world.isClient) return

        val connection = world.getBlockEntity(pos) as Connection
        val neighborConnection = world.getBlockEntity(neighborPos) as? Connection
        val neighborState = world.getBlockState(neighborPos)

        if (connection.connect(neighborConnection)) {
            world.updateNeighbors(pos, block)
        } else {
            neighborConnection?.also {
                if (it.connect(connection)) {
                    world.updateNeighbors(neighborPos, neighborState.block)
                }
            }
        }
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
        if (neighborState.isAir) {
            val connection = world.getBlockEntity(pos) as Connection
            connection.connectionData.sides.remove(direction)
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = blockEntityBuilder(pos, state)

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        super.onBroken(world, pos, state)

        val blockEntity = world.getBlockEntity(pos)
        if (blockEntity is ChildBlockEntity) {
            blockEntity.disconnect()
        }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(TranslatableText("block.slotlink.child.tooltip").formatted(Formatting.GRAY))
    }

}
