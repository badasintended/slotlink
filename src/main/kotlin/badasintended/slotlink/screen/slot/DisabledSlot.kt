package badasintended.slotlink.screen.slot

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class DisabledSlot(inventory: Inventory, index: Int) : Slot(inventory, index, Int.MIN_VALUE, Int.MIN_VALUE) {

    override fun canInsert(stack: ItemStack) = false
    override fun canTakeItems(playerEntity: PlayerEntity) = false

    @Environment(EnvType.CLIENT)
    override fun doDrawHoveringEffect() = false

}