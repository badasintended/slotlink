package badasintended.slotlink.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

class DummyInventory(
    private val size: Int
) : Inventory {

    var maxCount = 64

    private val stacks = DefaultedList.ofSize(size, ItemStack.EMPTY)

    override fun getMaxCountPerStack() = maxCount

    override fun size(): Int = size

    override fun canPlayerUse(player: PlayerEntity) = true

    override fun markDirty() {}

    override fun getStack(slot: Int): ItemStack = if (slot >= this.size()) ItemStack.EMPTY else stacks[slot]
    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(stacks, slot)
    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(stacks, slot, amount)

    override fun setStack(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
    }

    override fun isEmpty() = stacks.all { it.isEmpty }
    override fun clear() = stacks.clear()

}
