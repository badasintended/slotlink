package badasintended.slotlink.block.entity

import net.minecraft.block.BlockState
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

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        hasMaster = tag.getBoolean("hasMaster")
        masterPos = tag.getCompound("masterPos")
    }

}

class CableBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.CABLE)
