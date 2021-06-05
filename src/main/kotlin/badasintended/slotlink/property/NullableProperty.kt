package badasintended.slotlink.property

import badasintended.slotlink.property.NullableProperty.Value
import java.util.*
import net.minecraft.state.State
import net.minecraft.state.property.Property

fun <O, S, T : Comparable<T>> State<O, S>.with(property: NullableProperty<T>, actualValue: T?): S {
    val value = if (actualValue == null) property.nullValue else property.map[actualValue]!!
    return with(property, value)
}

fun <O, S, T : Comparable<T>> State<O, S>.getNull(property: NullableProperty<T>): T? {
    return get(property).value
}

@Suppress("UNCHECKED_CAST")
class NullableProperty<T : Comparable<T>>(
    private val property: Property<T>
) : Property<Value<T>>(property.name, Value::class.java as Class<Value<T>>) {

    class Value<T : Comparable<T>> internal constructor(
        val value: T?
    ) : Comparable<Value<T>> {

        override fun compareTo(other: Value<T>): Int {
            if (other === this || (value == null && other.value == null)) return 0

            if (value == null) return -1
            if (other.value == null) return 1

            return value.compareTo(other.value)
        }

    }

    private val values = linkedSetOf<Value<T>>()

    internal val map = hashMapOf<T, Value<T>>()
    internal val nullValue = Value<T>(null)

    init {
        values.add(nullValue)
        property.values.forEach {
            val value = Value(it)
            values.add(value)
            map[it] = value
        }
    }

    override fun getValues(): MutableCollection<Value<T>> = values

    override fun name(value: Value<T>) = value.value?.toString() ?: "null"

    override fun parse(name: String): Optional<Value<T>> {
        return if (name == "null") Optional.of(nullValue) else property.parse(name).map(map::get)
    }

}