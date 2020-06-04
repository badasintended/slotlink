package io.gitlab.intended.storagenetworks.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

class MasterBlockEntity : BlockEntity(ModBlockEntities.MASTER) {

    private var storagePos = ListTag()

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("storagePos", storagePos)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        storagePos = tag["storagePos"]!! as ListTag
    }

}
