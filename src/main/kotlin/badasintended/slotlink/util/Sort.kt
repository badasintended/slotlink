package badasintended.slotlink.util

import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

enum class Sort(val sorter: (ArrayList<ItemStack>) -> Any) {

    NAME({ it -> it.sortBy { it.name.string } }),
    NAME_DESC({ it -> it.sortByDescending { it.name.string } }),

    ID({ it -> it.sortBy { Registry.ITEM.getId(it.item).toString() } }),
    ID_DESC({ it -> it.sortByDescending { Registry.ITEM.getId(it.item).toString() } }),

    COUNT({ it -> it.sortBy { it.count } }),
    COUNT_DESC({ it -> it.sortByDescending { it.count } });

    val texture = tex("gui/sort_${name.toLowerCase()}")
    val translationKey = "container.slotlink.request.sort.${name.toLowerCase()}"

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
