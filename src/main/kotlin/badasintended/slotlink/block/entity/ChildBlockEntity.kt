package badasintended.slotlink.block.entity

import badasintended.slotlink.util.toNbt
import badasintended.slotlink.util.toPos
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

abstract class ChildBlockEntity(type: BlockEntityType<out BlockEntity>, pos: BlockPos, state: BlockState) :
    ModBlockEntity(type, pos, state) {

    var hasMaster = false
    var masterPos: BlockPos = BlockPos.ORIGIN
        set(value) {
            field = value.toImmutable()
        }

    override fun writeNbt(tag: NbtCompound): NbtCompound {
        super.writeNbt(tag)

        tag.putBoolean("hasMaster", hasMaster)
        tag.put("masterPos", masterPos.toNbt())

        return tag
    }

    override fun readNbt(tag: NbtCompound) {
        super.readNbt(tag)

        hasMaster = tag.getBoolean("hasMaster")
        masterPos = tag.getCompound("masterPos").toPos()
    }

}
