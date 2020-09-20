package badasintended.slotlink.util

enum class RedstoneMode {

    ON,
    POSITIVE,
    NEGATIVE,
    OFF;

    val texture = tex("gui/redstone_${name.toLowerCase()}")
    val tlKey = "container.slotlink.cable.redstone.${name.toLowerCase()}"

    companion object {

        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 3)]
    }

    fun next(): RedstoneMode {
        return values[(ordinal + 1) % values.size]
    }

}
