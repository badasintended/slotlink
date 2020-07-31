package badasintended.slotlink.block.entity

import badasintended.slotlink.common.util.toPos
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(type: BlockEntityType<out BlockEntity>) : ChildBlockEntity(type) {

    var linkedPos = CompoundTag()

    fun getLinkedInventory(world: WorldAccess): Inventory? {
        if (linkedPos == CompoundTag()) return null
        val linkedPos = linkedPos.toPos()
        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val blockEntity = world.getBlockEntity(linkedPos)

        if (!world.isBlockIgnored(linkedBlock)) {
            if (linkedBlock is InventoryProvider) {
                return linkedBlock.getInventory(linkedState, world, linkedPos)
            } else if (blockEntity is Inventory) {
                return blockEntity
            }
        }
        return null
    }

    protected abstract fun WorldAccess.isBlockIgnored(block: Block): Boolean

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkedPos", linkedPos)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        linkedPos = tag.getCompound("linkedPos")
    }

    override fun markRemoved() {
        super.markRemoved()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos.toPos())
            if (master is MasterBlockEntity) master.markDirty()
        }
    }

}
