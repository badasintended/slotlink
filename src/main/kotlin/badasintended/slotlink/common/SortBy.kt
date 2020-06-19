@file:Suppress("unused")

package badasintended.slotlink.common

enum class SortBy {
    NAME,
    IDENTIFIER,
    COUNT;

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 2)]
    }

    fun next(): SortBy {
        return values[(this.ordinal + 1) % values.size]
    }
}
