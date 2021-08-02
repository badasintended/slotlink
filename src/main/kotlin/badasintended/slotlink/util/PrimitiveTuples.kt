package badasintended.slotlink.util

data class ObjIntPair<T>(
    val first: T,
    val second: Int
)

infix fun <T> T.to(that: Int) = ObjIntPair(this, that)

data class ObjBoolPair<T>(
    val first: T,
    val second: Boolean
)

infix fun <T> T.to(that: Boolean) = ObjBoolPair(this, that)

data class IntPair(
    val first: Int,
    val second: Int
)

infix fun Int.to(that: Int) = IntPair(this, that)
