package badasintended.slotlink.network

import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtIntArray
import net.minecraft.nbt.NbtList
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState

class NetworkState : PersistentState() {

    companion object {

        operator fun get(world: ServerWorld): NetworkState {
            world as NetworkStateHolder
            return world.networkState
        }

        @JvmStatic
        fun create(world: ServerWorld, nbt: NbtCompound): NetworkState {
            return NetworkState().apply {
                if (nbt.contains("networks")) {
                    val networks = nbt.getList("networks", NbtElement.COMPOUND_TYPE.toInt())
                    networks.forEach { obj ->
                        obj as NbtCompound
                        val masterPos = obj.getIntArray("master").toPos()
                        map[masterPos] = Network(this, world, masterPos).also { network ->
                            val posses = obj.getList("pos", NbtElement.INT_ARRAY_TYPE.toInt())
                            posses.forEach { pos ->
                                pos as NbtIntArray
                                val arr = pos.intArray
                                network.map[arr.toPos()] = NodeType[arr[3]]
                            }
                        }
                    }
                }
            }
        }

    }

    val map = hashMapOf<BlockPos, Network>()

    operator fun get(pos: BlockPos) = map[pos]
    inline fun getOrPut(pos: BlockPos, default: () -> Network) = map.getOrPut(pos, default)
    fun remove(pos: BlockPos) = map.remove(pos)

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        if (map.isNotEmpty()) {
            val list = NbtList()
            map.forEach { (masterPos, network) ->
                if (!network.deleted && network.map.isNotEmpty()) {
                    val obj = NbtCompound()
                    obj.putIntArray("master", masterPos.toArray())
                    val posses = NbtList()
                    network.map.forEach { (pos, type) ->
                        if (type.save) {
                            posses.add(NbtIntArray(intArrayOf(pos.x, pos.y, pos.z, type.index)))
                        }
                    }
                    obj.put("pos", posses)
                    list.add(obj)
                }
            }
            nbt.put("networks", list)
        }
        return nbt
    }

}