package badasintended.slotlink.block.entity

import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.util.ObjBoolPair
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.to
import badasintended.slotlink.util.writeFilter
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos

abstract class FilteredBlockEntity(
    blockEntityType: BlockEntityType<out BlockEntity>,
    connectionType: ConnectionType<*>,
    pos: BlockPos,
    state: BlockState
) : ChildBlockEntity(blockEntityType, connectionType, pos, state),
    ExtendedScreenHandlerFactory {

    var blacklist = false
    var filter: DefaultedList<ObjBoolPair<ItemStack>> = DefaultedList.ofSize(9, ItemStack.EMPTY to false)

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

        nbt.putBoolean("isBlacklist", blacklist)

        val filterTag = NbtCompound()
        val list = NbtList()
        filter.forEachIndexed { i, pair ->
            if (!pair.first.isEmpty) {
                val compound = NbtCompound()
                compound.putByte("Slot", i.toByte())
                compound.putBoolean("matchNbt", pair.second)
                pair.first.writeNbt(compound)
                list.add(compound)
            }
        }
        filterTag.put("Items", list)
        nbt.put("filter", filterTag)
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        blacklist = nbt.getBoolean("isBlacklist")
        val filterTag = nbt.getCompound("filter")
        val list = filterTag.getList("Items", NbtType.COMPOUND)

        list.forEach {
            it as NbtCompound
            val slot = it.getByte("Slot").toInt()
            val matchNbt = it.getBoolean("matchNbt")
            if (slot in 0 until 9) {
                val stack = ItemStack.fromNbt(it)
                filter[slot] = stack to matchNbt
            }
        }
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.apply {
            bool(blacklist)
            writeFilter(filter)
        }
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.filter", pos.x, pos.y, pos.z)

}