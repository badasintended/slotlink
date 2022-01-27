package badasintended.slotlink.network

import net.minecraft.util.math.Direction

interface Node {

    var network: Network?
    val connection: Connection

    fun connect(adjacentNode: Node?): Boolean {
        val other = adjacentNode ?: return false

        val side = Direction.fromVector(connection.pos.subtract(other.connection.pos))!!
        connection.sides.add(side.opposite)
        other.connection.sides.add(side)

        if (network == other.network) return false
        val lastNetwork = network

        network.also { selfNetwork ->
            if (selfNetwork == null || selfNetwork.deleted) {
                this.network = other.network
                this.network?.add(this)
            }
        }
        return lastNetwork != network
    }

    fun disconnect() {
        network?.also { network ->
            network.remove(this)
            network.validate()
        }
        network = null
    }

    fun invalidate() {
        network?.invalidate(connection.type)
    }

}