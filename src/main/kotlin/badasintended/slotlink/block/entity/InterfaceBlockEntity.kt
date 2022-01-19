@file:Suppress("UnstableApiUsage")

package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.network.ConnectionType.Companion.MASTER
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.storage.FilteredItemStorage
import badasintended.slotlink.storage.FilteredItemStorage.Companion.EMPTY
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

private const val flag = FilterFlags.INSERT + FilterFlags.EXTRACT

class InterfaceBlockEntity(pos: BlockPos, state: BlockState) :
    FilteredBlockEntity(BlockEntityTypes.INTERFACE, ConnectionType.INTERFACE, pos, state) {

    @Suppress("UNUSED_PARAMETER")
    fun getStorage(unused: Direction): FilteredItemStorage {
        world?.also { world ->
            val master = network?.get(MASTER)?.firstOrNull() ?: return EMPTY
            val storages = master.getStorages(InterfaceBlockEntity::class, world, flag)
            return FilteredItemStorage(filter, blacklist, flag, CombinedStorage(storages.toList()))
        }
        return EMPTY
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity) = FilterScreenHandler(
        syncId, inv, blacklist, filter, ScreenHandlerContext.create(world, pos)
    )

}