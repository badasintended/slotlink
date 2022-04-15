package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ConnectorCableBlock
import badasintended.slotlink.network.Node
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.property.getNull
import badasintended.slotlink.storage.FilteredItemStorage
import badasintended.slotlink.util.int
import badasintended.slotlink.util.to
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Direction
import net.minecraft.world.WorldAccess

@Suppress("DEPRECATION", "UnstableApiUsage")
abstract class ConnectorCableBlockEntity(
    val block: ConnectorCableBlock,
    blockEntityType: BlockEntityType<out BlockEntity>,
    nodeType: NodeType<*>,
    pos: BlockPos,
    state: BlockState
) : FilteredBlockEntity(blockEntityType, nodeType, pos, state) {

    private var apiCache: BlockApiCache<Storage<ItemVariant>, Direction>? = null

    private var linkedSide = state.getNull(ConnectorCableBlock.CONNECTED)
        set(value) {
            apiCache = null
            field = value
        }

    private var linkedPos: BlockPos? = linkedSide?.let { pos.offset(it) }
        set(value) {
            apiCache = null
            field = value
        }

    var priority = 0
        set(value) {
            invalidate()
            field = value
        }

    fun getStorage(
        world: WorldAccess,
        side: Direction,
        flag: Int,
        master: MasterBlockEntity? = null,
        request: Boolean = false
    ): FilteredItemStorage {
        if (linkedPos == null) return FilteredItemStorage.EMPTY
        if (world !is ServerWorld) return FilteredItemStorage.EMPTY

        if (master != null && request) {
            val chunkPos = ChunkPos(pos)
            if (!world.forcedChunks.contains(chunkPos.toLong())) {
                master.forcedChunks.add(chunkPos.x to chunkPos.z)
            }
        }

        val linkedState = world.getBlockState(linkedPos)

        if (!block.isIgnored(linkedState)) {
            if (apiCache == null) apiCache = BlockApiCache.create(ItemStorage.SIDED, world, linkedPos)
            val storage = apiCache!!.find(side) ?: return FilteredItemStorage.EMPTY
            return FilteredItemStorage(filter, blacklist, flag, storage, priority, linkedPos!!)
        } else {
            return FilteredItemStorage.EMPTY
        }
    }

    override fun connect(adjacentNode: Node?): Boolean {
        return if (adjacentNode is InterfaceBlockEntity) false else super.connect(adjacentNode)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun setCachedState(state: BlockState) {
        super.setCachedState(state)
        linkedSide = state.getNull(ConnectorCableBlock.CONNECTED)
        linkedPos = linkedSide?.let { pos.offset(it) }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

        nbt.putInt("priority", priority)
        linkedSide?.let { nbt.putInt("link", it.id) }
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        linkedSide = if (nbt.contains("link")) Direction.byId(nbt.getInt("link")) else null
        linkedPos = linkedSide?.let { pos.offset(it) }
        priority = nbt.getInt("priority")
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        super.writeScreenOpeningData(player, buf)
        buf.apply {
            int(priority)
        }
    }

}
