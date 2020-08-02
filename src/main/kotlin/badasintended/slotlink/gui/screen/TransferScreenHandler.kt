package badasintended.slotlink.gui.screen

import badasintended.slotlink.common.registry.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import spinnery.widget.WSlot

class TransferScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val pos: BlockPos,
    var side: Direction,
    var isBlacklist: Boolean,
    var filter: DefaultedList<ItemStack>
) : ModScreenHandler(
    syncId, playerInventory
) {

    init {
        WSlot.addHeadlessPlayerInventory(root)
    }

    override fun getType() = ScreenHandlerRegistry.TRANSFER

}
