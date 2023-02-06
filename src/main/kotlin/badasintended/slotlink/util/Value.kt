package badasintended.slotlink.util

class Value<T>(
    private val value: T
) {

    fun get() = value

}

fun <T> T.wrap() = Value(this)
