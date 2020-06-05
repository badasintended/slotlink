package io.gitlab.intended.storagenetworks.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag

abstract class ConnectorCableBlockEntity(type: BlockEntityType<out BlockEntity>) : ChildBlockEntity(type) {

    private var linkedPos = CompoundTag()

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkedPos", linkedPos)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        linkedPos = tag.getCompound("linkedPos")
    }

}

class LinkCableBlockEntity : ConnectorCableBlockEntity(ModBlockEntities.LINK_CABLE)
