package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.Screens
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.item.MultiDimRemoteItem
import badasintended.slotlink.util.Sort
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity

class RemoteScreenHandler : RequestScreenHandler {

    private val offHand: Boolean

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        inventories: List<FilteredInventory>,
        lastSort: Sort,
        master: MasterBlockEntity,
        offHand: Boolean
    ) : super(syncId, playerInventory, inventories, lastSort, null, master) {
        this.offHand = offHand
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : super(syncId, playerInventory, buf) {
        this.offHand = buf.readBoolean()
    }

    override fun resize(viewedHeight: Int) {
        super.resize(viewedHeight)

        if (!offHand) if (playerInventory.mainHandStack.item is MultiDimRemoteItem) playerInventory.apply {
            slots[37 + selectedSlot] = object : Slot(this, selectedSlot, -999999, -999999) {
                override fun canInsert(stack: ItemStack) = false
                override fun canTakeItems(playerEntity: PlayerEntity) = false

                @Environment(EnvType.CLIENT)
                override fun doDrawHoveringEffect() = false
            }
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.REMOTE

    override fun close(player: PlayerEntity) {
        super.close(player)
        if (player is ServerPlayerEntity) {
            val stack = if (offHand) player.offHandStack else player.mainHandStack
            stack.orCreateTag.putInt("lastSort", lastSort.ordinal)
        }
    }

}
