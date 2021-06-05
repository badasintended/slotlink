package badasintended.slotlink.network

import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.PersistentState
import net.minecraft.world.World

class NetworkState(
    private val server: MinecraftServer,
    nbt: NbtCompound = NbtCompound()
) : PersistentState() {

    internal val map = hashMapOf<World, HashMap<BlockPos, Network>>()

    init {
        nbt.keys.forEach { worldId ->
            server.getWorld(RegistryKey.of(Registry.WORLD_KEY, Identifier(worldId)))?.let { world ->
                val list = nbt.getList(worldId, NbtType.COMPOUND)
                val networks = hashMapOf<BlockPos, Network>()
                list.forEach { obj ->
                    obj as NbtCompound
                    val master = obj.getIntArray("master").toPos()
                    networks[master] = Network(world, master).also { network ->
                        val posses = obj.getList("pos", NbtType.INT_ARRAY)
                        posses.forEach { pos ->
                            pos as NbtIntArray
                            network.add(ConnectionData(pos.intArray))
                        }
                    }
                }
                map[world] = networks
            }
        }
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        map.forEach { (world, networks) ->
            val list = NbtList()
            networks.forEach { (master, network) ->
                if (!network.deleted && network.map.isNotEmpty()) {
                    val obj = NbtCompound()
                    obj.putIntArray("master", master.toArray())
                    val posses = NbtList()
                    network.map.values.forEach { data ->
                        if (data.type.save) {
                            posses.add(NbtIntArray(data.toArray()))
                        }
                    }
                    obj.put("pos", posses)
                    list.add(obj)
                }
            }
            val worldId = world.registryKey.value
            nbt.put("${worldId.namespace}:${worldId.path}", list)
        }
        return nbt
    }

}