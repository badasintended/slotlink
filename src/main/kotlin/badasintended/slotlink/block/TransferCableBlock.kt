package badasintended.slotlink.block

import badasintended.slotlink.util.BlockEntityBuilder
import net.minecraft.block.Block
import net.minecraft.block.LeverBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

abstract class TransferCableBlock(id: String, builder: BlockEntityBuilder) : ConnectorCableBlock(id, builder) {

    override fun Block.isIgnored() = this is ModBlock

    override fun canConnect(world: WorldAccess, neighborPos: BlockPos): Boolean {
        val block = world.getBlockState(neighborPos).block
        return block is ModBlock || block is LeverBlock
    }

}
