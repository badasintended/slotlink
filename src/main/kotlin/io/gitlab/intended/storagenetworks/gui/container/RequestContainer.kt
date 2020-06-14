package io.gitlab.intended.storagenetworks.gui.container

import io.gitlab.intended.storagenetworks.gui.widget.WCraftingInputSlot
import io.gitlab.intended.storagenetworks.gui.widget.WCraftingOutputSlot
import io.gitlab.intended.storagenetworks.gui.widget.WInventoryPanel
import net.minecraft.container.CraftingTableContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.recipe.RecipeType
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import spinnery.common.utility.StackUtilities
import spinnery.widget.WSlot
import spinnery.widget.api.Action

class RequestContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    private val hasMaster = buf.readBoolean()
    private val totalInventory = if (hasMaster) buf.readInt() else 0

    private val inventoryPosSet = arrayListOf<BlockPos>()

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val inputSlots = HashSet<WCraftingInputSlot>()
    private val outputSlot: WCraftingOutputSlot

    private val invMap = HashMap<Int, Inventory>()

    val slotList = arrayListOf<WSlot>()

    var lastSort: WInventoryPanel.SortBy

    init {
        var lastSort = WInventoryPanel.SortBy.COUNT
        context.run { world, pos ->
            val blockEntity = world.getBlockEntity(pos)!!
            val nbt = blockEntity.toTag(CompoundTag())
            lastSort = WInventoryPanel.SortBy.of(nbt.getInt("lastSort"))
        }
        this.lastSort = lastSort

        for (i in 0 until totalInventory) inventoryPosSet.add(buf.readBlockPos())

        inventoryPosSet.forEachIndexed { index, blockPos ->
            invMap[index + 3] = world.getBlockEntity(blockPos)!! as Inventory
        }

        inventories[1] = craftingInv
        inventories[2] = resultInv
        inventories.putAll(invMap)

        val root = `interface`

        for (i in 0..8) {
            val slot = root.createChild { WCraftingInputSlot { craftItem() } }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
            inputSlots.add(slot)
        }

        invMap.forEach { (num, inv) ->
            for (i in 0 until inv.invSize) {
                val slot = root.createChild { WSlot() }
                slot.setInventoryNumber<WSlot>(num)
                slot.setSlotNumber<WSlot>(i)
                slotList.add(slot)
            }
        }

        outputSlot = root.createChild { WCraftingOutputSlot(inputSlots) }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        WSlot.addHeadlessPlayerInventory(root)
        root.recalculateCache()
    }

    fun saveLastSort(value: WInventoryPanel.SortBy) {
        context.run { world, pos ->
            val blockEntity = world.getBlockEntity(pos)!!
            val nbt = blockEntity.toTag(CompoundTag())
            nbt.putInt("lastSort", value.ordinal)
            blockEntity.fromTag(nbt)
            blockEntity.markDirty()
        }
    }

    fun isDeleted(invNumber: Int): Boolean {
        var deleted = false
        context.run { world, _ ->
            val state = world.getBlockState(inventoryPosSet[invNumber - 3])
            deleted = state.isAir
        }
        if (deleted) slotList.forEach { if (it.inventoryNumber == invNumber) it.setWhitelist<WSlot>() }
        return deleted
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

    /**
     * Make [Action.QUICK_MOVE] does not target crafting slots also
     * makes it target player inventory if the slot is one
     * of the [slotList] and vice versa.
     */
    override fun onSlotAction(
        slotNumber: Int,
        inventoryNumber: Int,
        button: Int,
        action: Action,
        player: PlayerEntity
    ) {
        val source: WSlot = `interface`.getSlot(inventoryNumber, slotNumber) ?: return
        if (source.isLocked) return

        if (action == Action.QUICK_MOVE) {
            val playerInvSlot = arrayListOf<WSlot>()
            val containerSlot = arrayListOf<WSlot>()
            val craftingSlot = arrayListOf<WSlot>()
            for (widget in serverInterface.allWidgets) {
                if (widget is WSlot) when (widget.inventoryNumber) {
                    0 -> playerInvSlot.add(widget)
                    1, 2 -> craftingSlot.add(widget)
                    else -> containerSlot.add(widget)
                }
            }

            val targets = arrayListOf<WSlot>()
            when (source.inventoryNumber) {
                0 -> {
                    targets.addAll(containerSlot)
                    targets.addAll(playerInvSlot)
                }
                1, 2 -> {
                    targets.addAll(playerInvSlot)
                    targets.addAll(containerSlot)
                }
                else -> targets.addAll(playerInvSlot)
            }

            for (target in targets) {
                if ((target.inventoryNumber == inventoryNumber) and (target.slotNumber == slotNumber)) continue
                if (target.refuses(source.stack) or target.isLocked) continue

                if ((!source.stack.isEmpty and target.stack.isEmpty) or (StackUtilities.equalItemAndTag(
                        source.stack, target.stack
                    ) and (target.stack.count < target.maxCount))
                ) {
                    val max = if (target.stack.isEmpty) source.maxCount else target.maxCount
                    source.consume(action, Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK)
                    StackUtilities.merge(source::getStack, target::getStack, source::getMaxCount) { max }
                        .apply({ source.setStack<WSlot>(it) }, { target.setStack<WSlot>(it) })
                    break
                }
            }
        } else super.onSlotAction(slotNumber, inventoryNumber, button, action, player)
    }

}
