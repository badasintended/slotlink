package badasintended.slotlink.gui.screen

import badasintended.slotlink.registry.NetworkRegistry
import badasintended.slotlink.registry.ScreenHandlerRegistry
import badasintended.slotlink.util.*
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction

class TransferScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    pos: BlockPos,
    priority: Int,
    isBlacklist: Boolean,
    filter: DefaultedList<ItemStack>,
    var side: Direction,
    var redstone: RedstoneMode
) : LinkScreenHandler(syncId, playerInventory, pos, priority, isBlacklist, filter) {

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInventory, buf.readBlockPos(), buf.readVarInt(), buf.readBoolean(), buf.readInventory(),
        Direction.byId(buf.readVarInt()), RedstoneMode.of(buf.readVarInt())
    )

    override fun save() {
        if (!world.isClient) return
        val buf = buf()
        buf.apply {
            writeBlockPos(pos)
            writeVarInt(priority)
            writeBoolean(isBlacklist)
            writeInventory(filter)
            writeVarInt(side.id)
            writeVarInt(redstone.ordinal)
        }

        c2s(NetworkRegistry.TRANSFER_WRITE, buf)
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.TRANSFER
    }

}
