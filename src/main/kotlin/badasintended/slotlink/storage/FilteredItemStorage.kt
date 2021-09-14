@file:Suppress("DEPRECATION", "UnstableApiUsage")

package badasintended.slotlink.storage

import badasintended.slotlink.util.ObjBoolPair
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage
import net.minecraft.item.ItemStack

class FilteredItemStorage(
    private val filter: List<ObjBoolPair<ItemStack>>,
    private val blacklist: Boolean,
    storage: Storage<ItemVariant>?
) : FilteringStorage<ItemVariant>(storage ?: Storage.empty()) {

    companion object {

        val EMPTY = FilteredItemStorage(emptyList(), true, null)

    }

    override fun canInsert(resource: ItemVariant) = if (filter.all { it.first.isEmpty }) true else {
        val equals = filter.filter { resource.matches(it.first) }

        if (equals.any { !it.second }) {
            !blacklist
        } else {
            val nbt = equals.filter { it.second && resource.matches(it.first) }
            if (blacklist) nbt.isEmpty() else nbt.isNotEmpty()
        }
    }

}
