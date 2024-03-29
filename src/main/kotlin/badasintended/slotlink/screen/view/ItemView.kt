package badasintended.slotlink.screen.view

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound

class ItemView(
    private var _item: Item,
    private var _nbt: NbtCompound?,
    var count: Int
) {

    companion object {

        val EMPTY = ItemView(Items.AIR, null, 0)
    }

    val item get() = _item
    val nbt get() = _nbt

    val isEmpty get() = item == Items.AIR || count <= 0

    private lateinit var _singleStack: ItemStack
    private var reloadSingleStack = true
    val singleStack: ItemStack
        get() {
            if (reloadSingleStack) {
                reloadSingleStack = false
                _singleStack = toStack(1)
            }
            return _singleStack
        }

    fun isItemAndTagEqual(stack: ItemStack): Boolean {
        if (isEmpty && stack.isEmpty) return true
        if (!stack.isOf(item)) return false

        if (!isEmpty && !stack.isEmpty) {
            return nbt == stack.nbt
        }

        return false
    }

    @Suppress("UnstableApiUsage")
    fun isItemAndTagEqual(view: StorageView<ItemVariant>): Boolean {
        if (isEmpty && (view.isResourceBlank || view.amount == 0L)) return true
        if (!view.resource.isOf(item)) return false

        if (!isEmpty && !(view.isResourceBlank || view.amount == 0L)) {
            return view.resource.nbtMatches(nbt)
        }

        return false
    }

    fun isItemAndTagEqual(stack: ItemView): Boolean {
        if (isEmpty && stack.isEmpty) return true
        if (item != stack.item) return false

        if (!isEmpty && !stack.isEmpty) {
            return nbt == stack.nbt
        }

        return false
    }

    fun update(item: Item, nbt: NbtCompound?, count: Int) {
        _item = item
        _nbt = nbt
        this.count = count
        reloadSingleStack = true
    }

    fun update(other: ItemView) = update(other.item, other.nbt?.copy(), other.count)

    fun toStack(count: Int = this.count): ItemStack {
        return ItemStack(item, count).also { it.nbt = nbt }
    }

    @Suppress("UnstableApiUsage")
    fun toVariant(): ItemVariant {
        return ItemVariant.of(item, nbt)
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

@Suppress("UnstableApiUsage")
fun StorageView<ItemVariant>.toView() = ItemView(resource.item, resource.nbt, amount.toInt())
