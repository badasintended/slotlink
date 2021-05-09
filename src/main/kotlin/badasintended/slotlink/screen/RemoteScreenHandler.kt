package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.Screens
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.item.MultiDimRemoteItem
import badasintended.slotlink.screen.slot.LockedSlot
import badasintended.slotlink.util.index
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

class RemoteScreenHandler : RequestScreenHandler {

    private val offHand: Boolean

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventories: Set<FilteredInventory>,
        master: MasterBlockEntity,
        offHand: Boolean
    ) : super(syncId, playerInventory, inventories, null, master) {
        this.offHand = offHand
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : super(syncId, playerInventory) {
        this.offHand = buf.readBoolean()
    }

    override fun resize(viewedHeight: Int, craft: Boolean) {
        super.resize(viewedHeight, craft)

        if (!offHand) if (playerInventory.mainHandStack.item is MultiDimRemoteItem) playerInventory.apply {
            slots.forEachIndexed { i, slot ->
                if (slot.stack == mainHandStack) slots[i] = LockedSlot(slot.inventory, slot.index, slot.x, slot.y)
            }
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.REMOTE

}
