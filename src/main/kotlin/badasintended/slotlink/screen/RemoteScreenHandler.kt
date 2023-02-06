package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.Screens
import badasintended.slotlink.screen.slot.LockedSlot
import badasintended.slotlink.storage.NetworkStorage
import badasintended.slotlink.util.int
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType

class RemoteScreenHandler : RequestScreenHandler {

    private val remoteSlot: Int

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        storage: NetworkStorage,
        master: MasterBlockEntity,
        remoteSlot: Int
    ) : super(syncId, playerInventory, storage, null, master) {
        this.remoteSlot = remoteSlot
    }

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : super(syncId, playerInventory) {
        this.remoteSlot = buf.int
    }

    override fun resize(viewedHeight: Int, craft: Boolean) {
        super.resize(viewedHeight, craft)

        if (remoteSlot >= 0) {
            val remote = playerInventory.getStack(remoteSlot)
            playerInventory.apply {
                slots.forEachIndexed { i, slot ->
                    if (slot.stack == remote) slots[i] = LockedSlot(slot.inventory, slot.index, slot.x, slot.y)
                }
            }
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.REMOTE

}
