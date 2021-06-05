package badasintended.slotlink.block.entity

import net.minecraft.block.entity.BlockEntity

interface BlockEntityWatcher<T : BlockEntity> {

    fun onRemoved()

}