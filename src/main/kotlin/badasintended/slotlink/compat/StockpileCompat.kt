package badasintended.slotlink.compat

import badasintended.slotlink.api.*
import badasintended.slotlink.util.isItemAndTagEqual
import me.branchpanic.mods.stockpile.content.block.TrashCanBlock
import me.branchpanic.mods.stockpile.content.blockentity.ItemBarrelBlockEntity
import me.branchpanic.mods.stockpile.impl.storage.MassItemStackStorage
import net.minecraft.item.ItemStack

object StockpileCompat : SlotlinkCompatInitializer {

    override fun dependencies() = arrayOf("stockpile")

    override fun initialize(compat: Compat) {
        compat.registerHandler(ItemBarrelBlockEntity::class.java, object : InventoryHandler<ItemBarrelBlockEntity>() {
            override fun size(t: ItemBarrelBlockEntity): Int {
                return (t.storage as MassItemStackStorage).maxStacks
            }

            override fun isValid(t: ItemBarrelBlockEntity, slot: Int, stack: ItemStack): Boolean {
                if (t.storage.isFull) return false
                if (t.storage.contents.reference.isEmpty) return true
                return stack.isItemAndTagEqual(t.storage.contents.reference)
            }

            override fun getStack(t: ItemBarrelBlockEntity, slot: Int): ItemStack {
                val stack = t.storage.contents.reference.copy()
                val filled = (t.storage.contents.amount / stack.maxCount).toInt()
                when {
                    slot < filled -> stack.count = stack.maxCount
                    slot == filled -> stack.count = (t.storage.contents.amount % stack.maxCount).toInt()
                    else -> stack.count = 0
                }
                return stack
            }

            override fun setStack(t: ItemBarrelBlockEntity, slot: Int, stack: ItemStack) {
                if (t.storage.contents.reference.isEmpty) t.setStack(1, stack) else {
                    t.storage.removeAtMost(getStack(t, slot).count.toLong())
                    t.storage.addAtMost(stack.count.toLong())
                }
                t.sync()
            }
        })

        compat.registerBlacklist(TrashCanBlock)
    }

}
