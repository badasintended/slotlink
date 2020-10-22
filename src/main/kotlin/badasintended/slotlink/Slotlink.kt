@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package badasintended.slotlink

import badasintended.slotlink.init.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object Slotlink {

    const val ID = "slotlink"

    private val init = listOf(BlockEntityTypes, Blocks, Items, Networks, Screens)

    fun main() {
        init.forEach { it.main() }
    }

    @Environment(EnvType.CLIENT)
    fun client() {
        init.forEach { it.client() }
    }

}
