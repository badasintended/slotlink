package io.gitlab.intended.storagenetworks.container

import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlainPanel
import io.gitlab.intended.storagenetworks.gui.widget.WCraftingResultSlot
import net.minecraft.container.BlockContext
import net.minecraft.container.CraftingTableContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.ContainerSlotUpdateS2CPacket
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RequestContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    companion object {
        /**
         * @param v position in grid, like in [WGridPanel]
         * @param o offset
         * @return position in pixel
         */
        fun g(v: Int, o: Int = 0): Int {
            return v * 18 + o
        }
    }

    private val hasMaster = buf.readBoolean()
    private val totalInventory = if (hasMaster) buf.readInt() else 0

    private val inventoryPosSet = HashSet<BlockPos>()

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    init {
        for (i in 0 until totalInventory) inventoryPosSet.add(buf.readBlockPos())

        val root = WPlainPanel()
        setRootPanel(root)

        val outputSlot = WCraftingResultSlot(player, craftingInv, resultInv, 0)
        val craftingSlot = WItemSlot.of(craftingInv, 0, 3, 3)

        root.add(outputSlot, g(6), g(13))
        root.add(craftingSlot, g(2), g(12))

        var lastX = 0
        var lastY = 0
        inventoryPosSet.forEach {
            val inventory = getBlockInventory(BlockContext.create(world, it))
            for (i in 0 until inventory.invSize) {
                val inventorySlot = WItemSlot.of(inventory, i)
                val x = lastX % 8
                val y = lastY / 8
                root.add(inventorySlot, g(x), g(y))
                lastX++
                lastY++
            }
        }

        root.add(createPlayerInventoryPanel(), 0, g(16))
        root.validate(this)
    }

    /**
     * Taken from [CraftingTableContainer]
     */
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

}
