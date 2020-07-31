package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.common.util.toTag
import net.minecraft.block.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class ChildBlock(id: String, settings: Settings = SETTINGS) : ModBlock(id, settings), BlockEntityProvider {

    // TODO: Optimize this part
    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        val blockEntity = world.getBlockEntity(pos) as ChildBlockEntity
        val neighborBlock = world.getBlockState(neighborPos).block
        val neighborBlockEntity = world.getBlockEntity(neighborPos)
        val currentlyHasMaster = blockEntity.hasMaster

        if (neighborBlockEntity is ChildBlockEntity) {
            val neighborHasMaster = neighborBlockEntity.hasMaster

            val currentMasterPos = blockEntity.masterPos
            val neighborMasterPos = neighborBlockEntity.masterPos

            if (currentlyHasMaster and !neighborHasMaster) {
                if (currentMasterPos == neighborMasterPos) {
                    blockEntity.hasMaster = false
                    blockEntity.markDirty()
                    world.updateNeighbors(pos, block)
                } else {
                    neighborBlockEntity.hasMaster = true
                    neighborBlockEntity.masterPos = currentMasterPos
                    neighborBlockEntity.markDirty()
                    world.updateNeighbors(neighborPos, neighborBlock)
                }
            } else if (!currentlyHasMaster and neighborHasMaster) {
                blockEntity.hasMaster = true
                blockEntity.masterPos = neighborMasterPos
                blockEntity.markDirty()
                world.updateNeighbors(pos, block)
            }
        } else if (neighborBlockEntity is MasterBlockEntity) {
            if (!currentlyHasMaster) {
                val masterPos = neighborPos.toTag()
                blockEntity.masterPos = masterPos
                blockEntity.hasMaster = true
                blockEntity.markDirty()
                world.updateNeighbors(pos, block)
            }
        } else if (currentlyHasMaster) {
            blockEntity.hasMaster = false
            blockEntity.markDirty()
            world.updateNeighbors(pos, block)
        }
    }

}
