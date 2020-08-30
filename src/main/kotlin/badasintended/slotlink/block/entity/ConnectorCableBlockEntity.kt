package badasintended.slotlink.block.entity

import badasintended.slotlink.api.Compat
import badasintended.slotlink.util.toPos
import badasintended.slotlink.util.writeInventory
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
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(type: BlockEntityType<out BlockEntity>) : ChildBlockEntity(type),
    ExtendedScreenHandlerFactory {

    var linkedPos = CompoundTag()

    var priority = 0

    var isBlackList = false

    var filter: DefaultedList<ItemStack> = DefaultedList.ofSize(9, ItemStack.EMPTY)

    fun getLinkedInventory(
        world: WorldAccess, master: MasterBlockEntity? = null, forceLoad: Boolean = false, compat: Boolean = false
    ): Pair<Inventory, Pair<Boolean, Set<Item>>>? {
        if (world !is World) return null
        if (linkedPos == CompoundTag()) return null
        val linkedPos = linkedPos.toPos()

        if (!world.isClient and (master != null) and forceLoad) {
            world as ServerWorld
            val chunkPos = ChunkPos(pos)
            if (!world.forcedChunks.contains(chunkPos.toLong())) {
                world.setChunkForced(chunkPos.x, chunkPos.z, true)
                master!!.forcedChunks.add(chunkPos.x to chunkPos.z)
            }
        }

        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val linkedBlockEntity = world.getBlockEntity(linkedPos)

        if (compat) {
            val registered = Compat.getRegisteredClass(linkedBlockEntity)
            if (registered != null) {
                return Compat.getHandler(registered, linkedBlockEntity) to (isBlackList to filter
                    .filterNot { it.isEmpty }
                    .map { it.item }
                    .toSet())
            }
        }

        if (!world.isBlockIgnored(linkedBlock)) when {
            (linkedBlock is ChestBlock) and (linkedBlockEntity is ChestBlockEntity) -> {
                linkedBlock as ChestBlock
                val inv = ChestBlock.getInventory(linkedBlock, linkedState, world, linkedPos, true) ?: return null
                return inv to (isBlackList to filter.filterNot { it.isEmpty }.map { it.item }.toSet())
            }
            linkedBlock is InventoryProvider -> {
                return linkedBlock.getInventory(linkedState, world, linkedPos) to (isBlackList to filter
                    .filterNot { it.isEmpty }
                    .map { it.item }
                    .toSet())
            }
            linkedBlockEntity is Inventory -> {
                return linkedBlockEntity to (isBlackList to filter.filterNot { it.isEmpty }.map { it.item }.toSet())
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
