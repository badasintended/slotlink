package badasintended.slotlink.block.entity

import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.ConnectionData
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.network.Network
import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos

abstract class ChildBlockEntity(
    blockEntityType: BlockEntityType<out BlockEntity>,
    connectionType: ConnectionType<*>,
    pos: BlockPos,
    state: BlockState
) : ModBlockEntity(blockEntityType, pos, state),
    Connection {

    override val connectionData = ConnectionData(pos, connectionType)

    private var lazyNetwork: Lazy<Network?>? = null
    private var _network: Network? = null
    override var network: Network?
        get() = _network ?: lazyNetwork?.value
        set(value) {
            _network = value
        }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)

        network?.also {
            if (!it.deleted) nbt.putIntArray("network", it.masterPos.toArray())
        }
        nbt.putInt("sides", connectionData.sideBits)

        return nbt
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        lazyNetwork = lazy {
            if (nbt.contains("network")) Network.get(world, nbt.getIntArray("network").toPos())
            else null
        }
        connectionData.sideBits = nbt.getInt("sides")
    }

    override fun markRemoved() {
        super.markRemoved()
        invalidate()
    }

}
