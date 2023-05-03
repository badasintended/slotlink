@file:Suppress("UnstableApiUsage")

package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.Node
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.storage.FilteredItemStorage
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

private const val flag = FilterFlags.INSERT + FilterFlags.EXTRACT

class InterfaceBlockEntity(pos: BlockPos, state: BlockState) :
    FilteredBlockEntity(BlockEntityTypes.INTERFACE, NodeType.INTERFACE, pos, state) {

    @Suppress("UNUSED_PARAMETER")
    fun getStorage(unused: Direction?): FilteredItemStorage {
        world?.also { world ->
            val master = network?.master ?: return FilteredItemStorage.EMPTY
            val storages = master.getStorages(world, flag)
            return FilteredItemStorage(filter, blacklist, flag, storages, 0)
        }
        return FilteredItemStorage.EMPTY
    }

    override fun connect(adjacentNode: Node?): Boolean {
        return if (adjacentNode is ConnectorCableBlockEntity) false else super.connect(adjacentNode)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity) = FilterScreenHandler(
        syncId, inv, blacklist, filter, ScreenHandlerContext.create(world, pos)
    )

}