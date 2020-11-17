package badasintended.slotlink.util

enum class RedstoneMode(
    private val id: String
) {

    ON("on"),
    POSITIVE("positive"),
    NEGATIVE("negative"),
    OFF("off");

    companion object {

        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 3)]

    }

    fun next(): RedstoneMode {
        return values[(ordinal + 1) % values.size]
    }

    override fun toString() = id

}
