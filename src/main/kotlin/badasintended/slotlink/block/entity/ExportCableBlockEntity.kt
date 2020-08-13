package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.*
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ExportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypeRegistry.EXPORT_CABLE) {

    override var side = Direction.UP

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val target = getLinkedInventory(world)?.first ?: return false

        val targetSlots = if (target is SidedInventory) target.getAvailableSlots(side).toList()
        else (0 until target.size()).toList()

        val inventories = master.getLinkedInventories(world).keys

        for (source in inventories) {
            for (j in 0 until source.size()) {
                val sourceStack = source.getStack(j)
                if (!sourceStack.isValid()) continue
                for (k in targetSlots) {
                    target.mergeStack(k, sourceStack, side)
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
            val master = world?.getBlockEntity(masterPos.toPos())

            if (master is MasterBlockEntity) {
                master.exportCables.add(pos.toTag())
                master.markDirty()
            }
        }
    }

}
