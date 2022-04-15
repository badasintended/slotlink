package badasintended.slotlink.block.entity

import badasintended.slotlink.config.config
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.Network
import badasintended.slotlink.network.Node
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.storage.NetworkStorage
import badasintended.slotlink.util.IntPair
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class MasterBlockEntity(pos: BlockPos, state: BlockState) :
    ModBlockEntity(BlockEntityTypes.MASTER, pos, state), Node {

    override val connection = Connection(pos, NodeType.MASTER)

    private val _network by lazy { Network.getOrCreate(world!!, pos) }
    override var network: Network?
        get() = _network
        set(_) {}

    var watchers = hashSetOf<Watcher>()

    private var tick = 0
    val forcedChunks = hashSetOf<IntPair>()

    fun getStorages(
        world: World,
        flag: Int,
        request: Boolean = false
    ): NetworkStorage {
        val linkCables = _network
            .get(NodeType.LINK) { list -> list.sortedByDescending { it.priority } }
        val storages = linkCables
            .mapTo(ArrayList(linkCables.size)) { it.getStorage(world, Direction.UP, flag, this, request) }

        return NetworkStorage(storages)
    }

    fun unmarkForcedChunks() = world?.let { world ->
        if (!world.isClient && watchers.isEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, false)
            }
            forcedChunks.clear()
        }
    }

    fun markForcedChunks() = world?.let { world ->
        if (!world.isClient && watchers.isNotEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, true)
            }
        }
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putInt("sides", connection.sideBits)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        connection.sideBits = nbt.getInt("sides")
    }

    override fun markRemoved() {
        super.markRemoved()
        invalidate()
        watchers.forEach { it.onMasterRemoved() }
    }

    object Ticker : BlockEntityTicker<MasterBlockEntity> {

        override fun tick(world: World, pos: BlockPos, state: BlockState, masterBlockEntity: MasterBlockEntity) {
            if (!world.isClient) masterBlockEntity.apply {
                tick++
                if (tick == 10) {
                    if (config.pauseTransferWhenOnScreen && watchers.isNotEmpty()) return
                    val cables = _network.get(NodeType.IMPORT) { list ->
                        list.sortedByDescending { it.priority }
                    }
                    for (cable in cables) {
                        if (cable.transfer(world, this)) break
                    }
                } else if (tick == 20) {
                    tick = 0
                    if (config.pauseTransferWhenOnScreen && watchers.isNotEmpty()) return
                    val cables = _network.get(NodeType.EXPORT) { list ->
                        list.sortedByDescending { it.priority }
                    }
                    for (cable in cables) {
                        if (cable.transfer(world, this)) break
                    }
                }
            }
        }

    }

    interface Watcher {

        fun onMasterRemoved()

    }

}
