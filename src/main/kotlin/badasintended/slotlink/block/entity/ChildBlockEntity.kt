package badasintended.slotlink.block.entity

import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.Network
import badasintended.slotlink.network.Node
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.util.toArray
import badasintended.slotlink.util.toPos
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class ChildBlockEntity(
    blockEntityType: BlockEntityType<out BlockEntity>,
    nodeType: NodeType<*>,
    pos: BlockPos,
    state: BlockState
) : ModBlockEntity(blockEntityType, pos, state),
    Node {

    override val connection = Connection(pos, nodeType)

    private var lazyNetworkPos: BlockPos? = null
    private var lazyNetwork: Lazy<Network?>? = null
    private var _network: Network? = null
    override var network: Network?
        get() = _network ?: lazyNetwork?.value
        set(value) {
            _network = value
        }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

        network?.also {
            if (!it.deleted) nbt.putIntArray("network", it.masterPos.toArray())
        }
        nbt.putInt("sides", connection.sideBits)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        lazyNetworkPos = if (nbt.contains("network")) nbt.getIntArray("network").toPos() else null
        connection.sideBits = nbt.getInt("sides")
    }

    override fun setWorld(world: World?) {
        super.setWorld(world)
        lazyNetwork = lazy {
            lazyNetworkPos?.let { Network.get(world, it) }
        }
    }

    override fun markRemoved() {
        super.markRemoved()
        invalidate()
    }

}
