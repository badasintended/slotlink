@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package io.gitlab.intended.storagenetworks

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.block.entity.type.BlockEntityTypeRegistry
import io.gitlab.intended.storagenetworks.client.gui.screen.ScreenRegistry
import io.gitlab.intended.storagenetworks.container.ContainerRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.util.Identifier
import java.util.logging.Level
import java.util.logging.Logger

object Mod {

    const val ID = "storagenetworks"
    const val NAME = "Storage Networks"

    val LOGGER: Logger = Logger.getLogger(ID)

    fun id(path: String) = Identifier(ID, path)
    fun log(level: Level, msg: String) = LOGGER.log(level, "[$NAME] $msg")

    fun main() {
        BlockRegistry.init()
        BlockEntityTypeRegistry.init()
        ContainerRegistry.init()
    }

    @Environment(EnvType.CLIENT)
    fun client() {
        ScreenRegistry.init()
    }

}
