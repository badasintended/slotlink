package io.gitlab.intended.storagenetworks.gui

import io.github.cottonmc.cotton.gui.ValidatedSlot
import io.gitlab.intended.storagenetworks.gui.widget.WCraftingResultSlot
import net.minecraft.container.CraftingResultSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.recipe.RecipeUnlocker

/**
 * [ValidatedSlot] with [CraftingResultSlot] logic
 * @see WCraftingResultSlot
 */
class CraftingResultValidatedSlot(
    private val player: PlayerEntity,
    private val craftingInventory: CraftingInventory,
    inv: Inventory,
    index: Int,
    x: Int,
    y: Int
) : ValidatedSlot(inv, index, x, y) {

    private var amount = 0

    override fun takeStack(amount: Int): ItemStack? {
        if (hasStack()) {
            this.amount += amount.coerceAtMost(stack.count)
        }
        return super.takeStack(amount)
    }

    override fun onCrafted(stack: ItemStack, amount: Int) {
        this.amount += amount
        onCrafted(stack)
    }

    override fun onTake(amount: Int) {
        this.amount += amount
    }

    override fun onCrafted(stack: ItemStack) {
        if (this.amount > 0) {
            stack.onCraft(player.world, player, this.amount)
        }
        if (inventory is RecipeUnlocker) {
            (inventory as RecipeUnlocker).unlockLastRecipe(player)
        }
        this.amount = 0
    }

    override fun onTakeItem(player: PlayerEntity, stack: ItemStack): ItemStack {
        onCrafted(stack)
        val defaultedList = player.world.recipeManager
            .getRemainingStacks(RecipeType.CRAFTING, craftingInventory, player.world)

        for (i in defaultedList.indices) {
            var itemStack: ItemStack = craftingInventory.getInvStack(i)
            val itemStack2 = defaultedList[i]
            if (!itemStack.isEmpty) {
                craftingInventory.takeInvStack(i, 1)
                itemStack = craftingInventory.getInvStack(i)
            }
            if (!itemStack2.isEmpty) {
                if (itemStack.isEmpty) {
                    craftingInventory.setInvStack(i, itemStack2)
                } else if (ItemStack.areItemsEqualIgnoreDamage(itemStack, itemStack2) and ItemStack.areTagsEqual(
                        itemStack,
                        itemStack2
                    )
                ) {
                    itemStack2.increment(itemStack.count)
                    craftingInventory.setInvStack(i, itemStack2)
                } else if (!this.player.inventory.insertStack(itemStack2)) {
                    this.player.dropItem(itemStack2, false)
                }
            }
        }
        return stack
    }

}
