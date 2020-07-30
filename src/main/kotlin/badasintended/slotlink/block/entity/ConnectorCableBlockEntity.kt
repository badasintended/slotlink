package badasintended.slotlink.block.entity

import badasintended.slotlink.common.*
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(
    type: BlockEntityType<out BlockEntity>,
    private val listKey: String
) : ChildBlockEntity(type) {

    var linkedPos = CompoundTag()

    fun getLinkedInventory(world: WorldAccess): Inventory? {
        if (linkedPos == CompoundTag()) return null
        val linkedPos = linkedPos.toPos()
        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val blockEntity = world.getBlockEntity(linkedPos)

        if (!world.isBlockIgnored(linkedBlock)) {
            if (linkedBlock.isInvProvider()) {
                return (linkedBlock as InventoryProvider).getInventory(linkedState, world, linkedPos)
            } else if (blockEntity.hasInv()) {
                return blockEntity!! as Inventory
            }
        }
        return null
    }

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
