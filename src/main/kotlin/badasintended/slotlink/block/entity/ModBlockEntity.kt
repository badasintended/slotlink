package badasintended.slotlink.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

abstract class ModBlockEntity(type: BlockEntityType<out BlockEntity>, pos: BlockPos, state: BlockState) :
    BlockEntity(type, pos, state) {

    override fun markDirty() {
        super.markDirty()

        val world = world as? ServerWorld ?: return
        world.chunkManager.markForUpdate(pos)
    }

}