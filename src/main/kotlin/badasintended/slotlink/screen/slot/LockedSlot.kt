package badasintended.slotlink.screen.slot

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class LockedSlot(inventory: Inventory, index: Int, x: Int = Int.MIN_VALUE, y: Int = Int.MIN_VALUE) :
    Slot(inventory, index, x, y) {

    override fun canInsert(stack: ItemStack) = false
    override fun canTakeItems(playerEntity: PlayerEntity) = false

}