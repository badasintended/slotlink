package badasintended.slotlink.inventory

import badasintended.slotlink.util.isItemAndTagEqual
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction

class FilteredInventory(
    private val filter: List<Pair<ItemStack, Boolean>>,
    private val blacklist: () -> Boolean
) : SidedInventory {

    var inventory: Inventory? = null
    private var slots = intArrayOf()

    val isNull get() = inventory == null

    val none get() = with(null)

    fun with(inventory: Inventory?): FilteredInventory {
        this.inventory = inventory
        slots = if (inventory == null) intArrayOf() else (0 until inventory.size()).toList().toIntArray()
        return this
    }

    fun merge(slot: Int, source: ItemStack, side: Direction) {
        var target = getStack(slot)
        while (target.count < target.maxCount && !source.isEmpty) {
            val one = source.copy()
            one.count = 1
            if (!isValid(slot, one)) return
            if (!canInsert(slot, one, side)) return
            if (target.isEmpty) {
                setStack(slot, one)
                source.decrement(1)
                target = getStack(slot)
            } else {
                if (!target.isItemAndTagEqual(source)) return
                target.increment(1)
                source.decrement(1)
            }
        }
    }

    fun isValid(stack: ItemStack): Boolean = if (stack.isEmpty || filter.all { it.first.isEmpty }) true else {
        val equals = filter.filter { it.first.isItemEqual(stack) }

        val blacklist = blacklist.invoke()

        if (equals.any { !it.second }) {
            !blacklist
        } else {
            val nbt = equals.filter { it.second && it.first.isItemAndTagEqual(stack) }
            if (blacklist) nbt.isEmpty() else nbt.isNotEmpty()
        }
    }

    override fun isValid(slot: Int, stack: ItemStack): Boolean {
        return isValid(stack) && inventory?.isValid(slot, stack) ?: false
    }

    override fun getMaxCountPerStack() = inventory?.maxCountPerStack ?: 64

    override fun onOpen(player: PlayerEntity) {
        inventory?.onOpen(player)
    }

    override fun onClose(player: PlayerEntity) {
        inventory?.onClose(player)
    }

    override fun getAvailableSlots(side: Direction): IntArray {
        return (inventory as? SidedInventory)?.getAvailableSlots(side) ?: slots
    }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return isValid(stack) && (inventory as? SidedInventory)?.canInsert(slot, stack, dir) ?: true
    }

    override fun canExtract(slot: Int, stack: ItemStack?, dir: Direction?): Boolean {
        return (inventory as? SidedInventory)?.canExtract(slot, stack, dir) ?: true
    }

    override fun clear() = inventory?.clear() ?: Unit

    override fun size() = inventory?.size() ?: 0

    override fun isEmpty() = inventory?.isEmpty ?: true

    override fun getStack(slot: Int): ItemStack = inventory?.getStack(slot) ?: ItemStack.EMPTY

    override fun removeStack(slot: Int, amount: Int): ItemStack =
        inventory?.removeStack(slot, amount) ?: ItemStack.EMPTY

    override fun removeStack(slot: Int): ItemStack = inventory?.removeStack(slot) ?: ItemStack.EMPTY

    override fun setStack(slot: Int, stack: ItemStack) = inventory?.setStack(slot, stack) ?: Unit

    override fun markDirty() = inventory?.markDirty() ?: Unit

    override fun canPlayerUse(player: PlayerEntity) = inventory?.canPlayerUse(player) ?: false

    override fun hashCode(): Int {
        val inventory = inventory
        return if (inventory is PairInventory) {
            inventory.first.hashCode() + inventory.second.hashCode()
        } else inventory.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilteredInventory

        val inv = inventory
        val oinv = other.inventory

        if (inv is PairInventory && oinv is PairInventory) {
            return oinv.isPart(inv.first) && oinv.isPart(inv.second)
        }

        return inv == oinv
    }

}
