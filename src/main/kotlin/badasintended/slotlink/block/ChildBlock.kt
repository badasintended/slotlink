package badasintended.slotlink.block

import badasintended.slotlink.common.pos2Tag
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
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
        val blockEntity = world.getBlockEntity(pos)
        val neighborState = world.getBlockState(neighborPos)
        val neighborBlock = neighborState.block

        val currentNbt = blockEntity!!.toTag(CompoundTag())
        val currentlyHasMaster = currentNbt.getBoolean("hasMaster")

        if (neighborBlock is ChildBlock) {
            val neighborBlockEntity = world.getBlockEntity(neighborPos)
            val neighborNbt = neighborBlockEntity!!.toTag(CompoundTag())
            val neighborHasMaster = neighborNbt.getBoolean("hasMaster")

            val masterPos = currentNbt.getCompound("masterPos")
            val neighborMasterPos = neighborNbt.getCompound("masterPos")

            if (currentlyHasMaster and !neighborHasMaster) {
                if (masterPos == neighborMasterPos) {
                    currentNbt.putBoolean("hasMaster", false)
                    blockEntity.fromTag(currentNbt)
                    blockEntity.markDirty()
                    world.updateNeighbors(pos, block)
                } else {
                    neighborNbt.putBoolean("hasMaster", true)
                    neighborNbt.put("masterPos", masterPos)
                    neighborBlockEntity.fromTag(neighborNbt)
                    neighborBlockEntity.markDirty()
                    world.updateNeighbors(neighborPos, neighborBlock)
                }
            } else if (!currentlyHasMaster and neighborHasMaster) {
                currentNbt.putBoolean("hasMaster", true)
                currentNbt.put("masterPos", neighborMasterPos)
                blockEntity.fromTag(currentNbt)
                blockEntity.markDirty()
                world.updateNeighbors(pos, block)
            }
        } else if (neighborBlock is MasterBlock) {
            if (!currentlyHasMaster) {
                val masterPos = pos2Tag(neighborPos)

                currentNbt.put("masterPos", masterPos)
                currentNbt.putBoolean("hasMaster", true)

                blockEntity.fromTag(currentNbt)
                blockEntity.markDirty()

                world.updateNeighbors(pos, block)
            }
        } else if (currentlyHasMaster) {
            currentNbt.putBoolean("hasMaster", false)
            blockEntity.fromTag(currentNbt)
            blockEntity.markDirty()
            world.updateNeighbors(pos, block)
        }
    }

}