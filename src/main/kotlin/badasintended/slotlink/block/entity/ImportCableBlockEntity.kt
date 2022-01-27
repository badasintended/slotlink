package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.storage.FilterFlags
import badasintended.slotlink.util.isEmpty
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage", "DEPRECATION")
class ImportCableBlockEntity(pos: BlockPos, state: BlockState) :
    TransferCableBlockEntity(Blocks.IMPORT_CABLE, BlockEntityTypes.IMPORT_CABLE, NodeType.IMPORT, pos, state) {

    override var side = Direction.DOWN

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val source = getStorage(world, side, FilterFlags.EXTRACT)
        if (!source.supportsExtraction()) return false

        Transaction.openOuter().use { transaction ->
            val targets = master.getStorages(this::class, world, FilterFlags.INSERT)
            for (view in source.iterable(transaction)) {
                if (view.isEmpty) continue
                val variant = view.resource
                val available = transaction.openNested().use { simulation ->
                    view.extract(variant, variant.item.maxCount.toLong(), simulation)
                }
                for (target in targets) {
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
