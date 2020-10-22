package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.Sort
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypes.REQUEST), ExtendedScreenHandlerFactory {

    var lastSort = Sort.NAME

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("lastSort", lastSort.ordinal)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        lastSort = Sort.of(tag.getInt("lastSort"))
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        val world = getWorld() ?: return null
        if (!hasMaster) return null
        val master = world.getBlockEntity(masterPos) ?: return null
        if (master !is MasterBlockEntity) return null
        val handler = RequestScreenHandler(syncId, inv, master.getInventories(world, true), lastSort, this, master)
        master.watchers.add(handler)
        master.markForcedChunks()
        return handler
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeVarInt(lastSort.ordinal)
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.request")

}
