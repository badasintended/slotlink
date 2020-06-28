package badasintended.slotlink.inventory

import badasintended.slotlink.client.gui.widget.WMultiSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

/**
 * Dummy inventory for combined stacks
 * @see WMultiSlot
 */
class DummyInventory(
    private val width: Int,
    private val height: Int
) : Inventory {

    private val stacks = DefaultedList.ofSize(height * width, ItemStack.EMPTY)

    override fun size(): Int = height * width

    override fun canPlayerUse(player: PlayerEntity) = true

    override fun markDirty() {}

    override fun getStack(slot: Int): ItemStack = if (slot >= this.size()) ItemStack.EMPTY else stacks[slot]
    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(stacks, slot)
    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(stacks, slot, amount)

    override fun setStack(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
    }

    override fun isEmpty() = stacks.none { !it.isEmpty }
    override fun clear() = stacks.clear()

}
