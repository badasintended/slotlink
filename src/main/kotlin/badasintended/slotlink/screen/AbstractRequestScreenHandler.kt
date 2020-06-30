package badasintended.slotlink.screen

import badasintended.slotlink.common.SortBy
import badasintended.slotlink.inventory.DummyInventory
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.BlockState
import net.minecraft.container.BlockContext
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
import spinnery.common.registry.NetworkRegistry.SLOT_UPDATE_PACKET
import spinnery.common.registry.NetworkRegistry.createSlotUpdatePacket
import spinnery.common.utility.StackUtilities
import spinnery.widget.WSlot
import spinnery.widget.api.Action

abstract class AbstractRequestScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModScreenHandler(syncId, player) {

    val blockPos: BlockPos = buf.readBlockPos()
    var lastSort = SortBy.of(buf.readInt())

    private val hasMaster = buf.readBoolean()

    private val totalInventory = if (hasMaster) buf.readInt() else 0

    private val inventoryPos = arrayListOf<BlockPos>()
    private val inventoryBlockEntity = arrayListOf<BlockState>()

    private val craftingInv = CraftingInventory(this, 3, 3)
    private val resultInv = CraftingResultInventory()

    private val inputSlots = HashSet<WSlot>()
    private val outputSlot: WSlot

    private val invMap = HashMap<Int, Inventory>()

    private val playerSlots = arrayListOf<WSlot>()
    val slotList = arrayListOf<WSlot>()

    private val context: BlockContext = BlockContext.create(player.world, blockPos)

    init {
        for (i in 0 until totalInventory) inventoryPos.add(buf.readBlockPos())

        inventoryPos.forEachIndexed { index, blockPos ->
            context.run { world, _ ->
                val chunk = world.getWorldChunk(blockPos)
                val blockEntity = chunk.getBlockEntity(blockPos)!!
                inventoryBlockEntity.add(chunk.getBlockState(blockPos))
                invMap[index + 3] = blockEntity as Inventory
            }
        }

        inventories[-2] = DummyInventory(1, 1)
        inventories[-1] = DummyInventory(8, 6)
        inventories[1] = craftingInv
        inventories[2] = resultInv
        inventories.putAll(invMap)

        WSlot.addHeadlessArray(root, 0, -1, 8, 6)
        WSlot.addHeadlessArray(root, 0, -2, 1, 1)

        for (i in 0..8) {
            val slot = root.createChild { WSlot() }
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

        outputSlot = root.createChild { WSlot() }
        outputSlot.setInventoryNumber<WSlot>(2)
        outputSlot.setSlotNumber<WSlot>(0)
        outputSlot.setWhitelist<WSlot>()

        playerSlots.addAll(WSlot.addHeadlessPlayerInventory(root))
        root.recalculateCache()
    }

    fun isDeleted(invNumber: Int): Boolean {
        var result = false
        context.run { world, _ ->
            val state = world.getBlockState(inventoryPos[invNumber - 3])
            result = (state != (inventoryBlockEntity[invNumber - 3]))
        }
        return result
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
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                    player, SLOT_UPDATE_PACKET,
                    createSlotUpdatePacket(syncId, outputSlot.slotNumber, outputSlot.inventoryNumber, itemStack)
                )
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
        val source: WSlot = root.getSlot(inventoryNumber, slotNumber) ?: return
        if (source.isLocked) return

        if (action == Action.QUICK_MOVE) {
            val playerInvSlot = arrayListOf<WSlot>()
            val containerSlot = arrayListOf<WSlot>()
            for (widget in root.allWidgets) {
                if (widget is WSlot) when (widget.inventoryNumber) {
                    0 -> playerInvSlot.add(widget)
                    -2, -1, 1, 2 -> Unit
                    else -> {
                        if (!isDeleted(widget.inventoryNumber)) containerSlot.add(widget)
                    }
                }
            }

            val targets = arrayListOf<WSlot>()
            when (inventoryNumber) {
                // when in player inventory, target container slots first
                0 -> {
                    targets.addAll(containerSlot)
                    targets.addAll(playerInvSlot)
                }

                // when in crafting slots, target player inventory first
                1, 2 -> {
                    targets.addAll(playerInvSlot)
                    targets.addAll(containerSlot)
                }

                // buffer
                -2 -> targets.addAll(containerSlot)

                // when in container slots, only target player inventory
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
                    if (((source.inventoryNumber == -2) or (source.inventoryNumber == 0)) and !source.stack.isEmpty) {
                        continue
                    } else break
                }
            }
            val buffer = root.getSlot<WSlot>(-2, 0)
            if ((inventoryNumber == -2) and !buffer.stack.isEmpty) {
                playerInventory.cursorStack = buffer.stack
                buffer.setStack<WSlot>(ItemStack.EMPTY)
            }
        } else super.onSlotAction(slotNumber, inventoryNumber, button, action, player)

        if ((inventoryNumber == 2) and outputSlot.stack.isEmpty) inputSlots.forEach {
            it.setStack<WSlot>(ItemStack(it.stack.item, (it.stack.count - 1)))
            craftItem()
        }
    }

    override fun close(player: PlayerEntity) {
        dropInventory(player, world, craftingInv)
        super.close(player)
    }

}
