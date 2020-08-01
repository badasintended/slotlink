package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.mergeStack
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ExportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypeRegistry.EXPORT_CABLE) {

    override var side = Direction.UP

    override fun transfer(world: World, master: MasterBlockEntity) {
        val target = getLinkedInventory(world) ?: return

        val targetSlots = if (target is SidedInventory) target.getAvailableSlots(side.opposite).toList()
        else (0 until target.size()).toList()

        val inventories = master.getLinkedInventories(world).values.filterNot { it.isEmpty }

        all@ for (source in inventories) {
            for (j in 0 until source.size()) {
                val sourceStack = source.getStack(j)
                if (!sourceStack.isValid()) continue
                for (k in targetSlots) {
                    if (target is SidedInventory) if (!target.canInsert(k, sourceStack, side.opposite)) continue
                    target.mergeStack(k, sourceStack)
                    source.markDirty()
                    target.markDirty()
                    if (sourceStack.isEmpty) break@all
                }
            }
        }
    }

}
