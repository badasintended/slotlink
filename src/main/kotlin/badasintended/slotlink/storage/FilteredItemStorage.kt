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
    val priority: Int,
    val differentiator: Any = Unit
) : FilteringStorage<ItemVariant>(storage) {

    companion object {

        val EMPTY = FilteredItemStorage(emptyList(), true, 0, Storage.empty(), 0)

    }

    private fun isValid(resource: ItemVariant): Boolean {
        if (filter.all { it.first.isEmpty }) return true

        filter.forEach {
            val stack = it.first
            val matchNbt = it.second

            if (resource.isOf(stack.item)) {
                if (!matchNbt || resource.nbtMatches(stack.nbt)) return !blacklist
            }
        }

        return blacklist
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
