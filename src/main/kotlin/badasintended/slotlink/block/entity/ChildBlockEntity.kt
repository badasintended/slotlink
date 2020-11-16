package badasintended.slotlink.block.entity

import badasintended.slotlink.util.toPos
import badasintended.slotlink.util.toTag
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos

abstract class ChildBlockEntity(type: BlockEntityType<out BlockEntity>) : BlockEntity(type) {

    var hasMaster = false
    var masterPos: BlockPos = BlockPos.ORIGIN
        set(value) {
            field = value.toImmutable()
        }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putBoolean("hasMaster", hasMaster)
        tag.put("masterPos", masterPos.toTag())

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        hasMaster = tag.getBoolean("hasMaster")
        masterPos = tag.getCompound("masterPos").toPos()
    }

}
