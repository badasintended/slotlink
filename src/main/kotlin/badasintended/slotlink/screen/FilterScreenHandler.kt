package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.FilteredBlockEntity
import badasintended.slotlink.init.Screens
import badasintended.slotlink.util.ObjBoolPair
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.readFilter
import badasintended.slotlink.util.to
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot

@Suppress("LeakingThis")
open class FilterScreenHandler(
    syncId: Int,
    playerInv: PlayerInventory,
    var blacklist: Boolean,
    val filter: MutableList<ObjBoolPair<ItemStack>>,
    private val context: ScreenHandlerContext
) : ScreenHandler(null, syncId) {

    constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInv, buf.bool, buf.readFilter(), ScreenHandlerContext.EMPTY
    )

    init {
        for (m in 0 until 3) for (l in 0 until 9) {
            addSlot(Slot(playerInv, l + m * 9 + 9, 8 + l * 18, 84 + m * 18))
        }
        for (m in 0 until 9) {
            addSlot(Slot(playerInv, m, 8 + m * 18, 142))
        }
    }

    fun filterSlotClick(i: Int, filterStack: ItemStack, matchNbt: Boolean) {
        val stack = filterStack.copy().apply { count = 1 }
        filter[i] = stack to (matchNbt && !filterStack.isEmpty)
    }

    open fun onClose(blockEntity: FilteredBlockEntity) {
        blockEntity.blacklist = blacklist
    }

    override fun quickMove(player: PlayerEntity, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            val s = if (index < 27) 27 else 0
            val e = if (index < 27) 36 else 27
            if (!insertItem(itemStack2, s, e, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }
            slot.onTakeItem(player, itemStack2)
        }
        return itemStack
    }

    override fun onClosed(player: PlayerEntity?) {
        super.onClosed(player)
        context.run { world, pos ->
            val be = world.getBlockEntity(pos)
            if (be is FilteredBlockEntity) {
                onClose(be)
                be.markDirty()
            }
        }
    }

    override fun canUse(player: PlayerEntity) = true

    override fun getType(): ScreenHandlerType<*> = Screens.FILTER

}