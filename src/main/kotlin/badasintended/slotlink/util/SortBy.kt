package badasintended.slotlink.util

import net.minecraft.util.Identifier

enum class SortBy(val texture: Identifier, val translationKey: String) {

    NAME(tex("gui/name"), "container.slotlink.request.sort.name"),
    IDENTIFIER(tex("gui/identifier"), "container.slotlink.request.sort.identifier"),
    COUNT(tex("gui/count"), "container.slotlink.request.sort.count");

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 2)]
    }

    fun next(): SortBy {
        return values[(ordinal + 1) % values.size]
    }
}
