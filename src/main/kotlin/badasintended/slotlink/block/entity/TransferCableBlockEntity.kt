package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.screen.TransferScreenHandler
import badasintended.slotlink.util.RedstoneMode
import badasintended.slotlink.util.RedstoneMode.NEGATIVE
import badasintended.slotlink.util.RedstoneMode.OFF
import badasintended.slotlink.util.RedstoneMode.ON
import badasintended.slotlink.util.RedstoneMode.POSITIVE
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

abstract class TransferCableBlockEntity(
    beType: BlockEntityType<out BlockEntity>,
    conType: ConnectionType<*>,
    pos: BlockPos,
    state: BlockState
) : ConnectorCableBlockEntity(beType, conType, pos, state) {

    var redstone = OFF

    abstract var side: Direction

    protected abstract fun transferInternal(world: World, master: MasterBlockEntity): Boolean

    fun transfer(world: World, master: MasterBlockEntity): Boolean {
        when (redstone) {
            OFF -> return false
            ON -> Unit
            POSITIVE -> if (world.getReceivedRedstonePower(pos) <= 0) return false
            NEGATIVE -> if (world.getReceivedRedstonePower(pos) > 0) return false
        }
        return transferInternal(world, master)
    }

    override fun Block.isIgnored() = this is ModBlock

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)

        side = Direction.byId(nbt.getInt("side"))
        redstone = RedstoneMode.of(nbt.getInt("redstone"))
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        super.writeNbt(nbt)

        nbt.putInt("side", side.id)
        nbt.putInt("redstone", redstone.ordinal)

        return nbt
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        return TransferScreenHandler(
            syncId, inv, priority, isBlackList, filter, side, redstone, ScreenHandlerContext.create(world, pos)
        )
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        super.writeScreenOpeningData(player, buf)
        buf.apply {
            writeVarInt(side.id)
            writeVarInt(redstone.ordinal)
        }
    }

}
