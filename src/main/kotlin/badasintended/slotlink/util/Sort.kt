package badasintended.slotlink.util

enum class Sort(id: String) {

    NAME("name"),
    NAME_DESC("name_desc"),

    ID("id"),
    ID_DESC("id_desc"),

    COUNT("count"),
    COUNT_DESC("count_desc");

    val texture = tex("gui/sort_$id")
    val translationKey = "container.slotlink.request.sort.$id"

    companion object {
        val values = values()
        fun of(i: Int) = values[i.coerceIn(0, 5)]
    }

    fun next(): Sort {
        return values[(ordinal + 1) % values.size]
    }

    fun inv(): Sort {
        return values[(ordinal + 3) % values.size]
    }

}
