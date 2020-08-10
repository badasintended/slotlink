package badasintended.slotlink.block.entity

import badasintended.slotlink.common.util.toPos
import badasintended.slotlink.common.util.writeInventory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.*
import net.minecraft.block.entity.*
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(type: BlockEntityType<out BlockEntity>) : ChildBlockEntity(type),
    ExtendedScreenHandlerFactory {

    var linkedPos = CompoundTag()

    var priority = 0

    var isBlackList = false

    var filter: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)

    fun getLinkedInventory(world: WorldAccess): Pair<Inventory, Pair<Boolean, Set<Item>>>? {
        if (linkedPos == CompoundTag()) return null
        val linkedPos = linkedPos.toPos()
        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val linkedBlockEntity = world.getBlockEntity(linkedPos)

        if (!world.isBlockIgnored(linkedBlock)) when {
            (linkedBlock is ChestBlock) and (linkedBlockEntity is ChestBlockEntity) -> {
                linkedBlock as ChestBlock
                val inv = ChestBlock.getInventory(linkedBlock, linkedState, world.world, linkedPos, true) ?: return null
                return Pair(inv, Pair(isBlackList, filter.filterNot { it.isEmpty }.map { it.item }.toSet()))
            }
            linkedBlock is InventoryProvider -> {
                return Pair(
                    linkedBlock.getInventory(linkedState, world, linkedPos),
                    Pair(isBlackList, filter.filterNot { it.isEmpty }.map { it.item }.toSet())
                )
            }
            linkedBlockEntity is Inventory -> {
                return Pair(
                    linkedBlockEntity, Pair(isBlackList, filter.filterNot { it.isEmpty }.map { it.item }.toSet())
                )
            }
        }
        return null
    }

    protected abstract fun WorldAccess.isBlockIgnored(block: Block): Boolean

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("priority", priority)
        tag.put("linkedPos", linkedPos)
        tag.putBoolean("isBlacklist", isBlackList)
        tag.put("filter", Inventories.toTag(CompoundTag(), filter))

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        priority = tag.getInt("priority")
        linkedPos = tag.getCompound("linkedPos")
        isBlackList = tag.getBoolean("isBlacklist")
        Inventories.fromTag(tag.getCompound("filter"), filter)
    }

    override fun markRemoved() {
        super.markRemoved()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos.toPos())
            if (master is MasterBlockEntity) master.markDirty()
        }
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeVarInt(priority)
        buf.writeBoolean(isBlackList)
        buf.writeInventory(filter)
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.cable")

}
