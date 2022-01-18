package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.FilteredBlockEntity
import badasintended.slotlink.block.entity.TransferCableBlockEntity
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode
import badasintended.slotlink.init.Screens
import badasintended.slotlink.util.ObjBoolPair
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.int
import badasintended.slotlink.util.readFilter
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.Direction

class TransferCableScreenHandler(
    syncId: Int,
    playerInv: PlayerInventory,
    blacklist: Boolean,
    filter: MutableList<ObjBoolPair<ItemStack>>,
    priority: Int,
    var side: Direction,
    var mode: Mode,
    context: ScreenHandlerContext
) : ConnectorCableScreenHandler(syncId, playerInv, blacklist, filter, priority, context) {

    constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) : this(
        syncId,
        playerInv,
        buf.bool,
        buf.readFilter(),
        buf.int,
        Direction.byId(buf.int),
        Mode.of(buf.int),
        ScreenHandlerContext.EMPTY
    )

    override fun onClose(blockEntity: FilteredBlockEntity) {
        super.onClose(blockEntity)
        if (blockEntity is TransferCableBlockEntity) {
            blockEntity.side = side
            blockEntity.mode = mode
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.TRANSFER_CABLE

}
