package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ExportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypes.EXPORT_CABLE) {

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
                for (k in targetSlots) {
                    if (!target.canInsert(k, sourceStack, side)) continue
                    target.merge(k, sourceStack, side)
                    source.markDirty()
                    target.markDirty()
                    if (sourceStack.isEmpty) return true
                }
            }
        }

        return false
    }

    override fun markDirty() {
        super.markDirty()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos)

            if (master is MasterBlockEntity) {
                master.exportPos.add(pos)
                master.markDirty()
            }
        }
    }

}
