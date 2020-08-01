@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package badasintended.slotlink

import badasintended.slotlink.common.registry.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Slotlink {

    const val ID = "slotlink"

    val LOGGER: Logger = LogManager.getLogger(ID)

    fun id(path: String) = Identifier(ID, path)

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
