package badasintended.slotlink.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.collection.DefaultedList

class SyncedInventory : Inventory {

    private val syncedStacks = DefaultedList.ofSize(1, ItemStack.EMPTY)

    override fun markDirty() {}

    override fun clear() {}

    override fun setStack(slot: Int, stack: ItemStack) {
        syncedStacks[0] = stack
    }

    override fun getStack(slot: Int): ItemStack = syncedStacks[0]

    override fun isEmpty() = syncedStacks[0].isEmpty

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(syncedStacks, 0, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(syncedStacks, 0)

    override fun canPlayerUse(player: PlayerEntity?) = true

    override fun size() = 1

}
