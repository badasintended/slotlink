package badasintended.slotlink.screen

import badasintended.slotlink.block.entity.ConnectorCableBlockEntity
import badasintended.slotlink.block.entity.FilteredBlockEntity
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

@Suppress("LeakingThis")
open class ConnectorCableScreenHandler(
    syncId: Int,
    playerInv: PlayerInventory,
    blacklist: Boolean,
    filter: MutableList<ObjBoolPair<ItemStack>>,
    var priority: Int,
    context: ScreenHandlerContext
) : FilterScreenHandler(syncId, playerInv, blacklist, filter, context) {

    constructor(syncId: Int, playerInv: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInv, buf.bool, buf.readFilter(), buf.int, ScreenHandlerContext.EMPTY
    )

    override fun onClose(blockEntity: FilteredBlockEntity) {
        super.onClose(blockEntity)
        if (blockEntity is ConnectorCableBlockEntity) {
            blockEntity.priority = priority
        }
    }

    override fun getType(): ScreenHandlerType<*> = Screens.CONNECTOR_CABLE

}
