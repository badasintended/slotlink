package io.gitlab.intended.storagenetworks.block.entity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag

abstract class ChildBlockEntity(type: BlockEntityType<out BlockEntity>) : BlockEntity(type) {

    private var hasMaster = false
    private var masterPos = CompoundTag()

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

class RequestBlockEntity : ChildBlockEntity(ModBlockEntities.REQUEST)

class CableBlockEntity : ChildBlockEntity(ModBlockEntities.CABLE)
