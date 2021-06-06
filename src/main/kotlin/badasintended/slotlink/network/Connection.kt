package badasintended.slotlink.network

import net.minecraft.util.math.Direction

interface Connection {

    var network: Network?
    val connectionData: ConnectionData

    fun connect(connection: Connection?): Boolean {
        val other = connection ?: return false

        val side = Direction.fromVector(connectionData.pos.subtract(other.connectionData.pos))!!
        connectionData.sides.add(side.opposite)
        other.connectionData.sides.add(side)

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
            network.validate()
        }
        network = null
    }

    fun invalidate() {
        network?.invalidate(connectionData.type)
    }

}