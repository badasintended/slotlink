package io.gitlab.intended.storagenetworks.block.entity

import io.gitlab.intended.storagenetworks.block.entity.type.BlockEntityTypeRegistry
import net.minecraft.nbt.CompoundTag

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST) {

    private var craftingInv = CompoundTag()
    private var outputInv = CompoundTag()

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("craftingInv", craftingInv)
        tag.put("outputInv", outputInv)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        craftingInv = tag.getCompound("craftingInv")
        outputInv = tag.getCompound("outputInv")
    }

}
