package io.gitlab.intended.storagenetworks.block.entity

import io.gitlab.intended.storagenetworks.block.entity.type.BlockEntityTypeRegistry
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag

abstract class ChildBlockEntity(type: BlockEntityType<out BlockEntity>) : BlockEntity(type) {

    protected var hasMaster = false
    protected var masterPos = CompoundTag()

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putBoolean("hasMaster", hasMaster)
        tag.put("masterPos", masterPos)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        hasMaster = tag.getBoolean("hasMaster")
        masterPos = tag.getCompound("masterPos")
    }

}

class CableBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.CABLE)

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST)
