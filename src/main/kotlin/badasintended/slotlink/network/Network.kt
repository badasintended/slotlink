package badasintended.slotlink.network

import kotlin.reflect.safeCast
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class Network internal constructor(
    val world: World,
    val masterPos: BlockPos
) {

    companion object {

        var state: NetworkState? = null

        fun get(world: World?, pos: BlockPos): Network? {
            if (world == null) return null
            if (world.isClient) return null
            return state!!.map.getOrPut(world, ::HashMap)[pos]
        }

        fun getOrCreate(world: World, pos: BlockPos): Network {
            if (world.isClient) return Network(world, pos)

            return state!!.map
                .getOrPut(world, ::HashMap)
                .getOrPut(pos) {
                    Network(world, pos)
                }
        }

    }

    private var _deleted = false
    val deleted get() = _deleted

    val map = hashMapOf<BlockPos, ConnectionData>()
    val cache = hashMapOf<ConnectionType<*>, List<Connection>>()

    val checked = hashSetOf<Connection>()

    init {
        add(ConnectionData(masterPos, ConnectionType.MASTER))
    }

    fun add(data: ConnectionData) {
        if (world.isClient) return
        map[data.pos] = data
        markDirty()
        invalidate(data.type)
    }

    fun remove(data: ConnectionData) {
        if (world.isClient) return
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
        state!!.map[world]?.remove(masterPos)
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
                remove(it.connectionData)
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
            val result = transformer(map
                .filterValues { it.type == type }
                .mapNotNull { type.clazz.safeCast(world.getBlockEntity(it.value.pos)) })
            map.keys.removeIf { map[it]!!.type == type }
            result.forEach {
                map[it.connectionData.pos] = it.connectionData
            }
            result
        } as List<T>
    }

}
