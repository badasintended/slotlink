package badasintended.slotlink.gui.screen

import badasintended.slotlink.common.registry.ScreenHandlerRegistry
import badasintended.slotlink.common.util.SortBy
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.util.math.BlockPos

class RemoteScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    invSet: Set<Inventory>,
    lastSort: SortBy,
    val offHand: Boolean,
    context: ScreenHandlerContext
) : RequestScreenHandler(
    syncId, playerInventory, BlockPos.ORIGIN, invSet, lastSort, context
) {

    override fun getType() = ScreenHandlerRegistry.REMOTE

}
