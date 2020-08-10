package badasintended.slotlink.gui.screen

import badasintended.slotlink.common.registry.ScreenHandlerRegistry
import badasintended.slotlink.common.util.SortBy
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.BlockPos

class RemoteScreenHandler : RequestScreenHandler {

    val offHand: Boolean

    constructor(
        syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf
    ) : super(syncId, playerInventory, buf) {
        this.offHand = buf.readBoolean()
    }

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory,
        invMap: Map<Inventory, Pair<Boolean, Set<Item>>>,
        lastSort: SortBy,
        offHand: Boolean,
        context: ScreenHandlerContext
    ) : super(syncId, playerInventory, BlockPos.ORIGIN, invMap, lastSort, context) {
        this.offHand = offHand
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.REMOTE
    }

}
