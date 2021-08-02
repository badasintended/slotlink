package badasintended.slotlink.screen.view

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound

class ItemView(
    private var _item: Item,
    private var _nbt: NbtCompound?,
    private var _count: Int
) {

    val item get() = _item
    val nbt get() = _nbt
    val count get() = _count

    val isEmpty get() = item == Items.AIR || count <= 0

    private lateinit var _renderStack: ItemStack
    private var reloadRenderStack = true
    val renderStack: ItemStack
        get() {
            if (reloadRenderStack) {
                reloadRenderStack = false
                _renderStack = toStack()
            }
            return _renderStack
        }

    fun isItemAndTagEqual(stack: ItemStack): Boolean {
        if (isEmpty && stack.isEmpty) return true
        if (!stack.isOf(item)) return false

        if (!isEmpty && !stack.isEmpty) {
            return nbt == stack.nbt
        }

        return false
    }

    fun update(item: Item, nbt: NbtCompound?, count: Int) {
        _item = item
        _nbt = nbt
        _count = count
        reloadRenderStack = true
    }

    fun update(stack: ItemStack) = update(stack.item, stack.nbt?.copy(), stack.count)

    fun update(other: ItemView) = update(other.item, other.nbt?.copy(), other.count)

    fun toStack(): ItemStack {
        return ItemStack(item, count).also { it.nbt = nbt }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ItemView) return false
        return item == other.item && count == other.count && nbt == other.nbt
    }

    override fun hashCode(): Int {
        var result = item.hashCode()
        result = 31 * result + (nbt?.hashCode() ?: 0)
        result = 31 * result + count
        return result
    }

}

fun ItemStack.toView() = ItemView(item, nbt?.copy(), count)
