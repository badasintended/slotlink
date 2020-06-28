@file:Suppress("unused")

package badasintended.slotlink.common

import badasintended.slotlink.Mod
import net.minecraft.util.Identifier

enum class SortBy(val texture: Identifier) {
    NAME(Mod.id("textures/gui/name.png")),
    IDENTIFIER(Mod.id("textures/gui/identifier.png")),
    COUNT(Mod.id("textures/gui/count.png"));

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 2)]
    }

    fun next(): SortBy {
        return values[(this.ordinal + 1) % values.size]
    }
}
