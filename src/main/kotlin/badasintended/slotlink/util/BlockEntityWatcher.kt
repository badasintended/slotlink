package badasintended.slotlink.util

import net.minecraft.block.entity.BlockEntity

interface BlockEntityWatcher<T : BlockEntity> {

    fun onRemoved()

}