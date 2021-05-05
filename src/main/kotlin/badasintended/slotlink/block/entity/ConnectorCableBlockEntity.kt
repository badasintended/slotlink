package badasintended.slotlink.block.entity

import badasintended.slotlink.api.Compat
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.util.toPos
import badasintended.slotlink.util.toTag
import badasintended.slotlink.util.writeFilter
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.ChestBlock
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(type: BlockEntityType<out BlockEntity>) : ChildBlockEntity(type), ExtendedScreenHandlerFactory {

    var linkedPos: BlockPos = BlockPos.ORIGIN
        set(value) {
            field = value.toImmutable()
        }

    var priority = 0

    var isBlackList = false

    var filter: DefaultedList<Pair<ItemStack, Boolean>> = DefaultedList.ofSize(9, ItemStack.EMPTY to false)

    private val filtered = FilteredInventory(filter) { isBlackList }

    fun getInventory(world: WorldAccess, master: MasterBlockEntity? = null, request: Boolean = false): FilteredInventory {
        if (world !is World) return filtered.none
        if (!hasMaster) return filtered.none

        if (!world.isClient && master != null && request) {
            world as ServerWorld
            val chunkPos = ChunkPos(pos)
            if (!world.forcedChunks.contains(chunkPos.toLong())) {
                master.forcedChunks.add(chunkPos.x to chunkPos.z)
            }
        }

        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val linkedBlockEntity = world.getBlockEntity(linkedPos)

        if (request) {
            val registered = Compat.getRegisteredClass(linkedBlockEntity)
            if (registered != null) {
                return filtered.with(Compat.getHandler(registered, linkedBlockEntity))
            }
        }

        if (!linkedBlock.isIgnored()) when {
            linkedBlock is ChestBlock && linkedBlockEntity is ChestBlockEntity -> {
                return filtered.with(ChestBlock.getInventory(linkedBlock, linkedState, world, linkedPos, true))
            }
            linkedBlock is InventoryProvider -> {
                return filtered.with(linkedBlock.getInventory(linkedState, world, linkedPos))
            }
            linkedBlockEntity is Inventory -> {
                return filtered.with(linkedBlockEntity)
            }
        }

        return filtered.none
    }

    protected abstract fun Block.isIgnored(): Boolean

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("priority", priority)
        tag.put("linkedPos", linkedPos.toTag())
        tag.putBoolean("isBlacklist", isBlackList)

        val filterTag = CompoundTag()
        val list = ListTag()
        filter.forEachIndexed { i, pair ->
            if (!pair.first.isEmpty) {
                val compound = CompoundTag()
                compound.putByte("Slot", i.toByte())
                compound.putBoolean("matchNbt", pair.second)
                pair.first.toTag(compound)
                list.add(compound)
            }
        }
        filterTag.put("Items", list)
        tag.put("filter", filterTag)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        priority = tag.getInt("priority")
        linkedPos = tag.getCompound("linkedPos").toPos()
        isBlackList = tag.getBoolean("isBlacklist")

        val filterTag = tag.getCompound("filter")
        val list = filterTag.getList("Items", NbtType.COMPOUND)

        list.forEach {
            it as CompoundTag
            val slot = it.getByte("Slot").toInt()
            val nbt = it.getBoolean("matchNbt")
            if (slot in 0 until 9) {
                val stack = ItemStack.fromTag(it)
                filter[slot] = stack to nbt
            }
        }
    }

    override fun markRemoved() {
        super.markRemoved()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos)
            if (master is MasterBlockEntity) master.markDirty()
        }
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.apply {
            writeVarInt(priority)
            writeBoolean(isBlackList)
            writeFilter(filter)
        }
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.cable", pos.x, pos.y, pos.z)

}
