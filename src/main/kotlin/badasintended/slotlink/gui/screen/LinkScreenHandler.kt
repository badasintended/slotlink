package badasintended.slotlink.gui.screen

import badasintended.slotlink.registry.NetworkRegistry
import badasintended.slotlink.registry.ScreenHandlerRegistry
import badasintended.slotlink.util.*
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import sbinnery.widget.WSlot

open class LinkScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    val pos: BlockPos,
    var priority: Int,
    var isBlacklist: Boolean,
    var filter: DefaultedList<ItemStack>
) : ModScreenHandler(
    syncId, playerInventory
) {

    constructor(syncId: Int, playerInventory: PlayerInventory, buf: PacketByteBuf) : this(
        syncId, playerInventory, buf.readBlockPos(), buf.readVarInt(), buf.readBoolean(), buf.readInventory()
    )

    init {
        WSlot.addHeadlessPlayerInventory(`interface`)
    }

    protected open fun save() {
        if (!world.isClient) return
        val buf = buf()
        buf.writeBlockPos(pos)
        buf.writeVarInt(priority)
        buf.writeBoolean(isBlacklist)
        buf.writeInventory(filter)

        ClientSidePacketRegistry.INSTANCE.sendToServer(NetworkRegistry.LINK_WRITE, buf)
    }

    override fun getType(): ScreenHandlerType<*> {
        return ScreenHandlerRegistry.LINK
    }

    override fun close(player: PlayerEntity) {
        super.close(player)
        save()
    }

}
