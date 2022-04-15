package badasintended.slotlink.storage

import badasintended.slotlink.config.config
import badasintended.slotlink.util.log
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

@Suppress("UnstableApiUsage")
class NetworkStorage(parts: MutableList<FilteredItemStorage>) :
    CombinedStorage<ItemVariant, FilteredItemStorage>(parts) {

    private val priorities = parts.stream().mapToInt { it.priority }.distinct().toArray()

    override fun supportsExtraction() = true
    override fun supportsInsertion() = true

    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext): Long {
        if (config.tryMergeStack) {
            // - when there's other storage that has the same priority,
            //   and it has the variant, try putting the variant to there first
            // - merge variants from multiple "slot" by extracting and re-inserting
            //   them immediately, this also moves them to earlier empty slot
            //   so that the actual variant that we want to move merges with them first
            // - prefer storages with higher priority even if there's
            //   lower priority storage that has the variant
            var remainder = maxAmount
            val visitedParts = hashSetOf<FilteredItemStorage>()
            merger@ for (priority in priorities) {
                for (part in parts) if (part.priority == priority) {
                    transaction.openNested().use { insideTransaction ->
                        val totalInside = part.extract(resource, Long.MAX_VALUE, insideTransaction)

                        if (totalInside > 0L) {
                            // in case the storage reduced its capacity making the insertion fail, highly unlikely
                            val reinserted = part.insert(resource, totalInside, insideTransaction)
                            if (reinserted != totalInside) {
                                log.error("slotlink: cannot reinsert all variants, skipping this storage")
                                insideTransaction.abort()
                            } else {
                                remainder -= part.insert(resource, remainder, insideTransaction)
                                visitedParts.add(part)
                                insideTransaction.commit()
                            }
                        }
                    }

                    if (remainder == 0L) break@merger
                }

                for (part in parts) if (part.priority == priority && !visitedParts.contains(part)) {
                    remainder -= part.insert(resource, remainder, transaction)
                    if (remainder == 0L) break@merger
                }
            }

            return maxAmount - remainder
        } else {
            return super.insert(resource, maxAmount, transaction)
        }
    }

}