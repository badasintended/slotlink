package badasintended.slotlink.init

import badasintended.slotlink.config.config
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

object EventListeners : Initializer {

    override fun main() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { _, _, _ ->
            config.invalidate()
        }
    }

}