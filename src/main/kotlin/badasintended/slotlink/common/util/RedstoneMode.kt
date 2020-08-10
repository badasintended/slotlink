package badasintended.slotlink.common.util

enum class RedstoneMode(id: String) {

    ON("on"),
    POSITIVE("positive"),
    NEGATIVE("negative"),
    OFF("off");

    val texture = tex("gui/redstone_$id")
    val tlKey = "container.slotlink.cable.redstone.$id"

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 3)]
    }

    fun next(): RedstoneMode {
        return values[(ordinal + 1) % values.size]
    }

}
