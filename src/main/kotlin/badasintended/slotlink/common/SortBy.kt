@file:Suppress("unused")

package badasintended.slotlink.common

import badasintended.slotlink.Mod
import net.minecraft.util.Identifier

enum class SortBy(
    val texture: Identifier,
    val translationKey: String
) {
    NAME(Mod.id("textures/gui/name.png"), "container.slotlink.request.sort.name"),
    IDENTIFIER(Mod.id("textures/gui/identifier.png"), "container.slotlink.request.sort.identifier"),
    COUNT(Mod.id("textures/gui/count.png"), "container.slotlink.request.sort.count");

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 2)]
    }

    fun next(): SortBy {
        return values[(this.ordinal + 1) % values.size]
    }
}
