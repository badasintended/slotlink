@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package badasintended.slotlink

import badasintended.slotlink.registry.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object Slotlink {

    const val ID = "slotlink"

    fun main() {
        BlockRegistry.init()
        ItemRegistry.init()
        BlockEntityTypeRegistry.init()
        ScreenHandlerRegistry.init()
        NetworkRegistry.initMain()
    }

    @Environment(EnvType.CLIENT)
    fun client() {
        ScreenRegistry.init()
        NetworkRegistry.initClient()
    }

}
