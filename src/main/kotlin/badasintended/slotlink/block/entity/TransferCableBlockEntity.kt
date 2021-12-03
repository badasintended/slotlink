package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ModBlock
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.NEGATIVE
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.OFF
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.ON
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.POSITIVE
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.screen.TransferCableScreenHandler
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

    var mode = OFF
    abstract var side: Direction

    protected abstract fun transferInternal(world: World, master: MasterBlockEntity): Boolean

    fun transfer(world: World, master: MasterBlockEntity): Boolean {
        when (mode) {
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
        mode = Mode.of(nbt.getInt("mode"))
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)

        nbt.putInt("side", side.id)
        nbt.putInt("mode", mode.ordinal)
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        return TransferCableScreenHandler(
            syncId, inv, priority, isBlackList, filter, side, mode, ScreenHandlerContext.create(world, pos)
        )
    }

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        super.writeScreenOpeningData(player, buf)
        buf.apply {
            writeVarInt(side.id)
            writeVarInt(mode.ordinal)
        }
    }

    enum class Mode(
        private val id: String
    ) {

        ON("on"),
        POSITIVE("positive"),
        NEGATIVE("negative"),
        OFF("off");

        companion object {

            val values = values()
            fun of(i: Int) = values[i.coerceIn(0, 3)]

        }

        fun next(): Mode {
            return values[(ordinal + 1) % values.size]
        }

        override fun toString() = id

    }

}
