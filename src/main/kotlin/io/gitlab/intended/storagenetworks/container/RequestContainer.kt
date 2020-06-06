package io.gitlab.intended.storagenetworks.container

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.gitlab.intended.storagenetworks.gui.widget.WCraftingResultSlot
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeFinder
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.world.World

class RequestContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    companion object {
        const val OUTPUT = 1
    }

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    init {
        val root = WGridPanel()
        setRootPanel(root)
        root.setSize(300, 200)

        val outputSlot = WCraftingResultSlot(player, craftingInv, resultInv, 0)
        root.add(outputSlot, 6, 2)

        val craftingSlot = WItemSlot.of(craftingInv, 0, 3, 3)
        root.add(craftingSlot, 2, 1)

        root.add(createPlayerInventoryPanel(), 0, 5)
        root.validate(this)
    }

    private fun craftItem(world: World) {
        if (!world.isClient) {
            player as ServerPlayerEntity
            var itemStack = ItemStack.EMPTY
            val optional = world.server!!.recipeManager.getFirstMatch(RecipeType.CRAFTING, craftingInv, world)
            if (optional.isPresent) {
                val craftingRecipe = optional.get()
                if (resultInv.shouldCraftRecipe(world, player, craftingRecipe)) {
                    itemStack = craftingRecipe.craft(craftingInv)
                }
            }
            resultInv.setInvStack(0, itemStack)
            player.networkHandler.sendPacket(ContainerSlotUpdateS2CPacket(syncId, 36, itemStack))
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        if (inventory == craftingInv) {
            context.run { world, _ -> craftItem(world) }
        } else super.onContentChanged(inventory)
    }

    override fun populateRecipeFinder(recipeFinder: RecipeFinder) = craftingInv.provideRecipeInputs(recipeFinder)
    override fun getCraftingWidth() = craftingInv.width
    override fun getCraftingHeight() = craftingInv.height
    override fun getCraftingResultSlotIndex() = 36
    override fun matches(recipe: Recipe<in Inventory>) = recipe.matches(craftingInv, player.world)

    override fun clearCraftingSlots() {
        craftingInv.clear()
        resultInv.clear()
    }

    @Environment(EnvType.CLIENT)
    override fun getCraftingSlotCount() = 10

}
