package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import badasintended.slotlink.util.isEmpty
import kotlin.math.min
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage", "DEPRECATION")
class ImportCableBlockEntity(pos: BlockPos, state: BlockState) :
    TransferCableBlockEntity(BlockEntityTypes.IMPORT_CABLE, ConnectionType.IMPORT, pos, state) {

    override var side = Direction.DOWN

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val source = getStorage(world, side)
        if (!source.supportsExtraction()) return false

        Transaction.openOuter().use { transaction ->
            val targets = master.getStorages(world)
            for (view in source.iterable(transaction)) {
                if (view.isEmpty) continue
                val variant = view.resource
                for (target in targets) {
                    val inserted = target.insert(variant, min(variant.item.maxCount.toLong(), view.amount), transaction)
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
