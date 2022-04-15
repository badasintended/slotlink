package badasintended.slotlink.block.entity

import badasintended.slotlink.block.ConnectorCableBlock
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.NEGATIVE
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.OFF
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.ON
import badasintended.slotlink.block.entity.TransferCableBlockEntity.Mode.POSITIVE
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.screen.TransferCableScreenHandler
import badasintended.slotlink.util.int
import badasintended.slotlink.util.isEmpty
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
abstract class TransferCableBlockEntity(
    block: ConnectorCableBlock,
    blockEntityType: BlockEntityType<out BlockEntity>,
    nodeType: NodeType<*>,
    pos: BlockPos,
    state: BlockState
) : ConnectorCableBlockEntity(block, blockEntityType, nodeType, pos, state) {

    var mode = OFF
    abstract var side: Direction

    protected abstract fun getSource(world: World, master: MasterBlockEntity): Storage<ItemVariant>
    protected abstract fun getTarget(world: World, master: MasterBlockEntity): Storage<ItemVariant>

    fun transfer(world: World, master: MasterBlockEntity): Boolean {
        when (mode) {
            OFF -> return false
            ON -> Unit
            POSITIVE -> if (world.getReceivedRedstonePower(pos) <= 0) return false
            NEGATIVE -> if (world.getReceivedRedstonePower(pos) > 0) return false
        }

        val source = getSource(world, master)
        if (!source.supportsExtraction()) return false

        val target = getTarget(world, master)
        if (!target.supportsInsertion()) return false

        Transaction.openOuter().use { transaction ->
            for (view in source.iterable(transaction)) {
                if (view.isEmpty) continue
                val variant = view.resource
                val available = transaction.openNested().use { simulation ->
                    view.extract(variant, variant.item.maxCount.toLong(), simulation)
                }
                val inserted = target.insert(variant, available, transaction)
                if (inserted > 0) {
                    view.extract(variant, inserted, transaction)
                    transaction.commit()
                    return true
                }
            }
        }

        return false
    }

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

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity) = TransferCableScreenHandler(
        syncId, inv, blacklist, filter, priority, side, mode, ScreenHandlerContext.create(world, pos)
    )

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        super.writeScreenOpeningData(player, buf)
        buf.apply {
            int(side.id)
            int(mode.ordinal)
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
