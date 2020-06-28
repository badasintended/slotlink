package badasintended.slotlink.inventory

import badasintended.slotlink.client.gui.widget.WMultiSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.DefaultedList

/**
 * Dummy inventory for combined stacks
 * @see WMultiSlot
 */
class DummyInventory(
    private val width: Int,
    private val height: Int
) : Inventory {

    private val stacks = DefaultedList.ofSize(height * width, ItemStack.EMPTY)

    override fun getInvSize(): Int = height * width

    override fun canPlayerUseInv(player: PlayerEntity) = true

    override fun markDirty() {}

    override fun getInvStack(slot: Int): ItemStack = if (slot >= this.invSize) ItemStack.EMPTY else stacks[slot]
    override fun removeInvStack(slot: Int): ItemStack = Inventories.removeStack(stacks, slot)
    override fun takeInvStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(stacks, slot, amount)

    override fun setInvStack(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
    }

    override fun isInvEmpty() = stacks.none { !it.isEmpty }
    override fun clear() = stacks.clear()

}
