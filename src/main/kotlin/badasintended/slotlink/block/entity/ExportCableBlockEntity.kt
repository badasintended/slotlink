package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.util.isEmpty
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage", "DEPRECATION")
class ExportCableBlockEntity(pos: BlockPos, state: BlockState) :
    TransferCableBlockEntity(BlockEntityTypes.EXPORT_CABLE, ConnectionType.EXPORT, pos, state) {

    override var side = Direction.UP

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val target = getStorage(world, side, FilterFlags.INSERT)
        if (!target.supportsInsertion()) return false

        val sources = master.getStorages(this::class, world, FilterFlags.EXTRACT)

        Transaction.openOuter().use { transaction ->
            for (source in sources) {
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
        }

        return false
    }

}
