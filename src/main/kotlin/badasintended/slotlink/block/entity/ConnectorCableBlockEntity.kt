package badasintended.slotlink.block.entity

import badasintended.slotlink.common.pos2Tag
import badasintended.slotlink.common.tag2Pos
import net.fabricmc.fabric.api.util.NbtType
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
            val masterBlockEntity = world.getBlockEntity(tag2Pos(masterPos))!!
            val masterNbt = masterBlockEntity.toTag(CompoundTag())

            val masterList = masterNbt.getList(listKey, NbtType.COMPOUND)
            masterList.add(pos2Tag(pos))

            masterNbt.put(listKey, masterList)
            masterBlockEntity.fromTag(masterNbt)
            masterBlockEntity.markDirty()
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkedPos", linkedPos)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        linkedPos = tag.getCompound("linkedPos")
    }

    override fun markDirty() {
        if (world != null) addToMasterConnectorList(world!!)
        super.markDirty()
    }

}

class LinkCableBlockEntity : ConnectorCableBlockEntity(BlockEntityTypeRegistry.LINK_CABLE, "linkCables")
