package badasintended.slotlink.block

import badasintended.slotlink.util.BlockEntityBuilder
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.LeverBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

abstract class TransferCableBlock(id: String, builder: BlockEntityBuilder) : ConnectorCableBlock(id, builder) {

    override fun isIgnored(block: Block) = false

    override fun connect(
        state: BlockState,
        direction: Direction,
        world: WorldAccess,
        neighborState: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        val fromSuper = super.connect(state, direction, world, neighborState, neighborPos)
        val block = neighborState.block
        return fromSuper.with(PROPERTIES[direction], block is ModBlock || block is LeverBlock)
    }

}
