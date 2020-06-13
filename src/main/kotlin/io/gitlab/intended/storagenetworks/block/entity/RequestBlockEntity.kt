package io.gitlab.intended.storagenetworks.block.entity

import net.minecraft.nbt.CompoundTag

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST) {

    private var lastSort = 0

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("lastSort", lastSort)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        lastSort = tag.getInt("lastSort")
    }

}
