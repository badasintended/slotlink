package badasintended.slotlink.block

import net.minecraft.block.Block
import net.minecraft.block.LeverBlock
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

abstract class TransferCableBlock(id: String, be: () -> BlockEntity) : ConnectorCableBlock(id, be) {

    override fun Block.isIgnored() = this is ModBlock

    override fun canConnect(world: WorldAccess, neighborPos: BlockPos): Boolean {
        val block = world.getBlockState(neighborPos).block
        return (block is ModBlock) or (block is LeverBlock)
    }

}
