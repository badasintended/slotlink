package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.TransferCableBlockEntity
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode
import badasintended.slotlink.init.Screens
import badasintended.slotlink.util.ObjBoolPair
import badasintended.slotlink.util.readFilter
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.Direction

class TransferCableScreenHandler(
    syncId: Int,
    playerInv: PlayerInventory,
    priority: Int,
    blacklist: Boolean,
    filter: MutableList<ObjBoolPair<ItemStack>>,
    var side: Direction,
    var mode: Mode,
    context: ScreenHandlerContext
) : ConnectorCableScreenHandler(syncId, playerInv, priority, blacklist, filter, context) {

    constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInv,
        buf.readVarInt(),
        buf.readBoolean(),
        buf.readFilter(),
        Direction.byId(buf.readVarInt()),
        Mode.of(buf.readVarInt()),
        ScreenHandlerContext.EMPTY
    )

    override fun close(player: PlayerEntity) {
        super.close(player)
        context.run { world, pos ->
            val be = world.getBlockEntity(pos)
            if (be is TransferCableBlockEntity) {
                be.side = side
                be.mode = mode
                be.markDirty()
            }
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.TRANSFER_CABLE

}
