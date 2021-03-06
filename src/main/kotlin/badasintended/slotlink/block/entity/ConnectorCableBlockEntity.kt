package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ConnectorCableBlock
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.property.getNull
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
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class ConnectorCableBlockEntity(
    blockEntityType: BlockEntityType<out BlockEntity>,
    connectionType: ConnectionType<*>,
    pos: BlockPos,
    state: BlockState
) : ChildBlockEntity(blockEntityType, connectionType, pos, state),
    ExtendedScreenHandlerFactory,
    Connection {

    private var linkedSide = state.getNull(ConnectorCableBlock.CONNECTED)
    private var linkedPos: BlockPos? = linkedSide?.let { pos.offset(it) }

    var priority = 0

    var isBlackList = false

    var filter: DefaultedList<Pair<ItemStack, Boolean>> = DefaultedList.ofSize(9, ItemStack.EMPTY to false)

    private val filtered = FilteredInventory(filter) { isBlackList }

    fun getInventory(
        world: WorldAccess,
        master: MasterBlockEntity? = null,
        request: Boolean = false
    ): FilteredInventory {
        if (world !is World) return filtered.none

        if (!world.isClient && master != null && request) {
            world as ServerWorld
            val chunkPos = ChunkPos(pos)
            if (!world.forcedChunks.contains(chunkPos.toLong())) {
                master.forcedChunks.add(chunkPos.x to chunkPos.z)
            }
        }

        if (linkedPos == null) return filtered.none

        val linkedState = world.getBlockState(linkedPos)
        val linkedBlock = linkedState.block
        val linkedBlockEntity = world.getBlockEntity(linkedPos)

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

    @Suppress("DEPRECATION")
    override fun setCachedState(state: BlockState) {
        super.setCachedState(state)
        linkedSide = state.getNull(ConnectorCableBlock.CONNECTED)
        linkedPos = linkedSide?.let { pos.offset(it) }
    }

    protected abstract fun Block.isIgnored(): Boolean

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)

        nbt.putInt("priority", priority)
        nbt.putBoolean("isBlacklist", isBlackList)

        linkedSide?.let { nbt.putInt("link", it.id) }

        val filterTag = NbtCompound()
        val list = NbtList()
        filter.forEachIndexed { i, pair ->
            if (!pair.first.isEmpty) {
                val compound = NbtCompound()
                compound.putByte("Slot", i.toByte())
                compound.putBoolean("matchNbt", pair.second)
                pair.first.writeNbt(compound)
                list.add(compound)
            }
        }
        filterTag.put("Items", list)
        nbt.put("filter", filterTag)

        return nbt
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        linkedSide = if (nbt.contains("link")) Direction.byId(nbt.getInt("link")) else null
        linkedPos = linkedSide?.let { pos.offset(it) }

        priority = nbt.getInt("priority")
        isBlackList = nbt.getBoolean("isBlacklist")

        val filterTag = nbt.getCompound("filter")
        val list = filterTag.getList("Items", NbtType.COMPOUND)

        list.forEach {
            it as NbtCompound
            val slot = it.getByte("Slot").toInt()
            val matchNbt = it.getBoolean("matchNbt")
            if (slot in 0 until 9) {
                val stack = ItemStack.fromNbt(it)
                filter[slot] = stack to matchNbt
            }
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
