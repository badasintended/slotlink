package badasintended.slotlink.block.entity

import badasintended.slotlink.config.config
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.ConnectionData
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.network.ConnectionType.Companion.EXPORT
import badasintended.slotlink.network.ConnectionType.Companion.IMPORT
import badasintended.slotlink.network.ConnectionType.Companion.LINK
import badasintended.slotlink.network.Network
import badasintended.slotlink.storage.FilteredItemStorage
import badasintended.slotlink.util.IntPair
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class MasterBlockEntity(pos: BlockPos, state: BlockState) :
    ModBlockEntity(BlockEntityTypes.MASTER, pos, state), Connection {

    override val connectionData = ConnectionData(pos, ConnectionType.MASTER)

    private val _network by lazy { Network.getOrCreate(world!!, pos) }
    override var network: Network?
        get() = _network
        set(_) {}

    var watchers = hashSetOf<Watcher>()

    private val invSet = ObjectLinkedOpenHashSet<FilteredItemStorage>()

    private var tick = 0
    val forcedChunks = hashSetOf<IntPair>()

    fun getStorages(world: World, flag: Int, request: Boolean = false): MutableSet<FilteredItemStorage> {
        invSet.clear()
        _network
            .get(LINK) { list -> list.sortedByDescending { it.priority } }
            .forEach { invSet.add(it.getStorage(world, Direction.UP, flag, this, request)) }
        return invSet
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
        nbt.putInt("sides", connectionData.sideBits)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        connectionData.sideBits = nbt.getInt("sides")
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
                    val cables = _network.get(IMPORT) { list ->
                        list.sortedByDescending { it.priority }
                    }
                    for (cable in cables) {
                        if (cable.transfer(world, this)) break
                    }
                } else if (tick == 20) {
                    tick = 0
                    if (config.pauseTransferWhenOnScreen && watchers.isNotEmpty()) return
                    val cables = _network.get(EXPORT) { list ->
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
