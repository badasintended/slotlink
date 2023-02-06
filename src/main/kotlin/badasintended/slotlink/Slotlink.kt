@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package badasintended.slotlink

import badasintended.slotlink.compat.trinkets.TrinketsInit
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.EventListeners
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.KeyBinds
import badasintended.slotlink.init.Packets
import badasintended.slotlink.init.Screens
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object Slotlink {

    const val ID = "slotlink"

    private val init = listOf(
        BlockEntityTypes,
        Blocks,
        EventListeners,
        Items,
        KeyBinds,
        Packets,
        Screens,

        TrinketsInit
    )

    fun main() {
        init.forEach { it.main() }
    }

    @Environment(EnvType.CLIENT)
    fun client() {
        init.forEach { it.client() }
    }

}
