package badasintended.slotlink.block.entity

import badasintended.slotlink.common.toPos
import badasintended.slotlink.common.toTag
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.World

abstract class ConnectorCableBlockEntity(
    type: BlockEntityType<out BlockEntity>,
    private val listKey: String
) : ChildBlockEntity(type) {

    private var linkedPos = CompoundTag()

    private fun addToMasterConnectorList(world: World) {
        if (hasMaster) {
            val masterPos = masterPos.toPos()
            val masterBlockState = world.getBlockState(masterPos)
            val masterBlockEntity = world.getBlockEntity(masterPos)!!
            val masterNbt = masterBlockEntity.toTag(CompoundTag())

            val masterList = masterNbt.getList(listKey, NbtType.COMPOUND)
            masterList.add(pos.toTag())

            masterNbt.put(listKey, masterList)
            masterBlockEntity.fromTag(masterBlockState, masterNbt)
            masterBlockEntity.markDirty()
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkedPos", linkedPos)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        linkedPos = tag.getCompound("linkedPos")
    }

    override fun markDirty() {
        if (world != null) addToMasterConnectorList(world!!)
        super.markDirty()
    }

}

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE, "linkCables")
