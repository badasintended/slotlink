package badasintended.slotlink.util

import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

enum class Sort(
    private val id: String,
    val sorter: (ArrayList<ItemStack>) -> Any
) {

    NAME("name", { it -> it.sortBy { it.name.string } }),
    NAME_DESC("name_desc", { it -> it.sortByDescending { it.name.string } }),

    ID("id", { it -> it.sortBy { Registry.ITEM.getId(it.item).toString() } }),
    ID_DESC("id_desc", { it -> it.sortByDescending { Registry.ITEM.getId(it.item).toString() } }),

    COUNT("count", { it -> it.sortBy { it.count } }),
    COUNT_DESC("count_desc", { it -> it.sortByDescending { it.count } });

    companion object {

        val values = values()

    }

    fun next(): Sort {
        return values[(ordinal + 1) % values.size]
    }

    override fun toString() = id

}
