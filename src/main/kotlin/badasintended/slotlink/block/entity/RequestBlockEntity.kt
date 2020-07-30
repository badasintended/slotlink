package badasintended.slotlink.block.entity

import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST) {

    var lastSort = 0

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("lastSort", lastSort)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        lastSort = tag.getInt("lastSort")
    }

}
