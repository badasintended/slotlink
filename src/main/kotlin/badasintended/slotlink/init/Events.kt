package badasintended.slotlink.init

import badasintended.slotlink.network.Network
import badasintended.slotlink.network.NetworkState
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

object Events : Initializer {

    override fun main() {
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            Network.state = server.overworld.persistentStateManager.getOrCreate(
                { NetworkState(server, it) },
                { NetworkState(server) },
                "slotlink"
            )
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {
            Network.state = null
        }
    }

}