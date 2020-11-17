package badasintended.slotlink.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.server.world.ServerWorld

abstract class ModBlockEntity(type: BlockEntityType<out BlockEntity>) : BlockEntity(type) {

    override fun markDirty() {
        super.markDirty()

        val world = world as? ServerWorld ?: return
        world.chunkManager.markForUpdate(pos)
    }

}