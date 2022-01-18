@file:Suppress("UnstableApiUsage")

package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.storage.FilteredItemStorage
import java.util.*
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class InterfaceBlockEntity(pos: BlockPos, state: BlockState) :
    ChildBlockEntity(BlockEntityTypes.INTERFACE, ConnectionType.INTERFACE, pos, state),
    Storage<ItemVariant> {

    private val insertStorageCache = StorageCache(FilterFlags.INSERT)
    private val extractStorageCache = StorageCache(FilterFlags.EXTRACT)

    override fun insert(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext) =
        insertStorageCache[transaction].insert(resource, maxAmount, transaction)

    override fun extract(resource: ItemVariant, maxAmount: Long, transaction: TransactionContext) =
        extractStorageCache[transaction].extract(resource, maxAmount, transaction)

    override fun iterator(transaction: TransactionContext): MutableIterator<StorageView<ItemVariant>> =
        extractStorageCache[transaction].iterator(transaction)

    inner private class StorageCache(private val flags: Int) {

        private val instances = WeakHashMap<TransactionContext, Storage<ItemVariant>>()

        @Synchronized
        operator fun get(transaction: TransactionContext): Storage<ItemVariant> =
            world?.takeUnless { it.isClient }?.let { world ->
                network?.get(ConnectionType.MASTER)?.firstOrNull()?.let { master ->
                    instances.computeIfAbsent(transaction) {
                        getInner(world, master)
                    }
                }
            } ?: FilteredItemStorage.EMPTY

        private fun getInner(world: World, master: MasterBlockEntity): Storage<ItemVariant> =
            CombinedStorage(
                master.getStorages(
                    InterfaceBlockEntity::class,
                    world,
                    flags,
                    false
                ).toList()
            )
    }
}