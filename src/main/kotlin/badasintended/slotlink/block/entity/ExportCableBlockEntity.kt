package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.ConnectionType
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ExportCableBlockEntity(pos: BlockPos, state: BlockState) :
    TransferCableBlockEntity(BlockEntityTypes.EXPORT_CABLE, ConnectionType.EXPORT, pos, state) {

    override var side = Direction.UP

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val target = getInventory(world)
        if (target.isNull) return false

        val targetSlots = target.getAvailableSlots(side)
        val inventories = master.getInventories(world)

        for (source in inventories) {
            for (j in 0 until source.size()) {
                val sourceStack = source.getStack(j)
                if (sourceStack.isEmpty) continue
                val lastCount = sourceStack.count
                for (k in targetSlots) {
                    target.merge(k, sourceStack, side)
                    if (sourceStack.count < lastCount) {
                        source.markDirty()
                        target.markDirty()
                        if (sourceStack.isEmpty) return true
                    }
                }
            }
        }

        return false
    }

}
