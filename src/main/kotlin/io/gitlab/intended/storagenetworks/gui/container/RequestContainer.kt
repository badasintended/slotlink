package io.gitlab.intended.storagenetworks.gui.container

import io.gitlab.intended.storagenetworks.gui.widget.WCraftingInputSlot
import io.gitlab.intended.storagenetworks.gui.widget.WCraftingOutputSlot
import io.gitlab.intended.storagenetworks.gui.widget.WInventorySlot
import net.minecraft.container.CraftingTableContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import spinnery.widget.WSlot

class RequestContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    private val hasMaster = buf.readBoolean()
    private val totalInventory = if (hasMaster) buf.readInt() else 0

    private val inventoryPosSet = HashSet<BlockPos>()

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val outputSlot: WCraftingOutputSlot

    private val invMap = HashMap<Int, Inventory>()
    val slotList = arrayListOf<WSlot>()

    init {
        for (i in 0 until totalInventory) inventoryPosSet.add(buf.readBlockPos())

        inventoryPosSet.forEachIndexed { index, blockPos ->
            invMap[index + 4] = world.getBlockEntity(blockPos)!! as Inventory
        }

        inventories[1] = craftingInv
        inventories[2] = resultInv
        inventories[3] = CraftingResultInventory()
        inventories.putAll(invMap)

        val root = `interface`

        val craftingSlots = HashSet<WCraftingInputSlot>()
        for (i in 0..8) {
            val slot = root.createChild { WCraftingInputSlot { craftItem() } }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
            slot.setLocked<WSlot>(true)
            craftingSlots.add(slot)
        }

        invMap.forEach { (num, inv) ->
            for (i in 0 until (inv.invSize)) {
                val slot = root.createChild { WInventorySlot() }
                slot.setInventoryNumber<WSlot>(num)
                slot.setSlotNumber<WSlot>(i)
                slotList.add(slot)
            }
        }

        outputSlot = root.createChild { WCraftingOutputSlot(craftingSlots) }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        WSlot.addHeadlessPlayerInventory(root)
        root.recalculateCache()
    }

    /**
     * Taken from [CraftingTableContainer]
     */
    private fun craftItem() {
        context.run { world, _ ->
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
                outputSlot.setStack<WSlot>(itemStack)
                outputSlot.setPreviewStack<WSlot>(itemStack)
                resultInv.unlockLastRecipe(player)
            }
        }
    }

    override fun onContentChanged(inventory: Inventory) {
        if ((inventory == craftingInv) or (inventory == resultInv)) {
            craftItem()
        } else super.onContentChanged(inventory)
    }

}
