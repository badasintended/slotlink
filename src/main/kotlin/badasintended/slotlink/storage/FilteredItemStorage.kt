@file:Suppress("DEPRECATION", "UnstableApiUsage")

package badasintended.slotlink.storage

import badasintended.slotlink.util.ObjBoolPair
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.item.ItemStack

class FilteredItemStorage(
    private val filter: List<ObjBoolPair<ItemStack>>,
    private val blacklist: Boolean,
    private val flag: Int,
    storage: Storage<ItemVariant>,
    val differentiator: Any = Unit
) : FilteringStorage<ItemVariant>(storage) {

    companion object {

        val EMPTY = FilteredItemStorage(emptyList(), true, 0, Storage.empty())

    }

    private fun isValid(resource: ItemVariant) = if (filter.all { it.first.isEmpty }) true else {
        val equals = filter.filter { resource.matches(it.first) }

        if (equals.any { !it.second }) {
            !blacklist
        } else {
            val nbt = equals.filter { it.second && resource.matches(it.first) }
            if (blacklist) nbt.isEmpty() else nbt.isNotEmpty()
        }
    }

    override fun canInsert(resource: ItemVariant) = (flag and FilterFlags.INSERT) == 0 || isValid(resource)
    override fun canExtract(resource: ItemVariant) = (flag and FilterFlags.EXTRACT) == 0 || isValid(resource)

    override fun iterator(transaction: TransactionContext): MutableIterator<StorageView<ItemVariant>> {
        return if (flag and FilterFlags.EXTRACT == 0) {
            backingStorage.get().iterator(transaction)
        } else {
            super.iterator(transaction)
        }
    }

}
