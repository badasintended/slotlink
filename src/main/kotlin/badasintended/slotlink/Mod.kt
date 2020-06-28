@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package badasintended.slotlink

import badasintended.slotlink.block.BlockRegistry
import badasintended.slotlink.block.entity.BlockEntityTypeRegistry
import badasintended.slotlink.client.gui.screen.ScreenRegistry
import badasintended.slotlink.network.NetworkRegistry
import badasintended.slotlink.screen.ScreenHandlerRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import java.util.logging.Level
import java.util.logging.Logger

object Mod {

    const val ID = "slotlink"

    val LOGGER: Logger = Logger.getLogger(ID)

    fun id(path: String) = Identifier(ID, path)
    fun log(msg: String, level: Level = Level.INFO) = LOGGER.log(level, "[$ID] $msg")

    fun main() {
        BlockRegistry.init()
        //ItemRegistry.init()
        BlockEntityTypeRegistry.init()
        ScreenHandlerRegistry.init()
        NetworkRegistry.initMain()
    }

    @Environment(EnvType.CLIENT)
    fun client() {
        ScreenRegistry.init()
    }

}
