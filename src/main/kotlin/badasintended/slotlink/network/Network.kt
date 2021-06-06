package badasintended.slotlink.network

import kotlin.reflect.safeCast
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Network internal constructor(
    val state: NetworkState?,
    val world: World,
    val masterPos: BlockPos
) {

    companion object {

        fun get(world: World?, pos: BlockPos): Network? {
            if (world !is ServerWorld) return null
            return NetworkState[world][pos]
        }

        fun getOrCreate(world: World, pos: BlockPos): Network {
            if (world !is ServerWorld) return Network(null, world, pos)

            val state = NetworkState[world]
            return state.getOrPut(pos) {
                Network(state, world, pos)
            }
        }

    }

    private var _deleted = false
    val deleted get() = _deleted

    val map = hashMapOf<BlockPos, ConnectionType<*>>()
    val cache = hashMapOf<ConnectionType<*>, List<Connection>>()

    init {
        map[masterPos] = ConnectionType.MASTER
    }

    fun add(connection: Connection) {
        if (world.isClient) return
        val data = connection.connectionData
        map[data.pos] = data.type
        markDirty()
        invalidate(data.type)
    }

    fun remove(connection: Connection) {
        if (world.isClient) return
        val data = connection.connectionData
        map.remove(data.pos)
        markDirty()
        invalidate(data.type)
    }

    fun invalidate(type: ConnectionType<*>) {
        if (world.isClient) return
        cache.remove(type)
    }

    fun delete() {
        if (world.isClient) return
        _deleted = true
        map.clear()
        cache.clear()
        state?.remove(masterPos)
        markDirty()
    }

    fun markDirty() {
        if (world.isClient) return
        state?.isDirty = true
    }

    fun validate() {
        val unvisited = HashSet(map.keys)

        fun visit(pos: BlockPos) {
            if (!unvisited.contains(pos)) return
            unvisited.remove(pos)

            get(pos) {
                val data = it.connectionData
                data.sides.forEach { side ->
                    visit(pos.offset(side))
                }
            }
        }

        visit(masterPos)

        unvisited.forEach { pos ->
            get(pos) {
                remove(it)
                it.network = null
            }
        }
    }

    inline fun get(pos: BlockPos, consumer: (Connection) -> Unit) {
        if (world.isClient || !map.containsKey(pos)) return
        val be = world.getBlockEntity(pos)
        (be as? Connection)?.apply {
            consumer(this)
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <T : Connection> get(
        type: ConnectionType<T>,
        transformer: (List<T>) -> List<T> = { it }
    ): List<T> {
        if (world.isClient) return emptyList()
        return cache.getOrPut(type) {
            transformer(map
                .filterValues { it == type }
                .mapNotNull { type.clazz.safeCast(world.getBlockEntity(it.key)) })
        } as List<T>
    }

}
