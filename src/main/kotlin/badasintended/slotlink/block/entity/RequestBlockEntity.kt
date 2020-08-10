package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.SortBy
import badasintended.slotlink.common.util.toPos
import badasintended.slotlink.gui.screen.RequestScreenHandler
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST), ExtendedScreenHandlerFactory {

    var lastSort = 0

    private var inventories = emptyMap<Inventory, Pair<Boolean, Set<Item>>>()
    private var _masterPos = BlockPos.ORIGIN

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("lastSort", lastSort)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        lastSort = tag.getInt("lastSort")
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        val world = getWorld() ?: return null
        if (!hasMaster) return null
        _masterPos = masterPos.toPos()
        val master = world.getBlockEntity(_masterPos) ?: return null
        if (master !is MasterBlockEntity) return null
        inventories = master.getLinkedInventories(world)
        val handler = RequestScreenHandler(
            syncId, inv, _masterPos, inventories, SortBy.of(lastSort), ScreenHandlerContext.create(world, _masterPos)
        )
        master.watchers.add(handler)
        return handler
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(_masterPos)
        buf.writeVarInt(lastSort)
        buf.writeIntArray(inventories.keys.stream().mapToInt { it.size() }.toArray())
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.request")

}
