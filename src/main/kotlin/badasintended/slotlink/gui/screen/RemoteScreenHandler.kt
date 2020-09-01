package badasintended.slotlink.gui.screen

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.registry.ScreenHandlerRegistry
import badasintended.slotlink.util.Sort
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

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
        lastSort: Sort,
        offHand: Boolean,
        world: World,
        master: MasterBlockEntity
    ) : super(syncId, playerInventory, BlockPos.ORIGIN, invMap, lastSort, world, master) {
        this.offHand = offHand
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.REMOTE
    }

}
