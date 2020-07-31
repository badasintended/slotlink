package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import net.minecraft.inventory.SidedInventory
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import spinnery.common.utility.StackUtilities

class ExportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypeRegistry.EXPORT_CABLE) {

    override var side = Direction.UP

    override fun transfer(world: World, master: MasterBlockEntity) {
        val target = getLinkedInventory(world) ?: return

        val targetSlots =
            if (target is SidedInventory) target.getAvailableSlots(side.opposite).toList()
            else (0 until target.size()).toList()

        val inventories = master.getLinkedInventories(world).values.filterNot { it.isEmpty }

        all@ for (source in inventories) {
            for (j in 0 until source.size()) {
                val sourceStack = source.getStack(j)
                if (!sourceStack.isValid()) continue
                for (k in targetSlots) {
                    if (target is SidedInventory) if (!target.canInsert(k, sourceStack, side.opposite)) continue
                    val targetStack = target.getStack(k)
                    StackUtilities.merge(sourceStack, targetStack, sourceStack.maxCount, targetStack.maxCount)
                        .apply({
                            source.setStack(j, it)
                            sourceStack.count = it.count
                        }, { target.setStack(k, it) })
                    source.markDirty()
                    target.markDirty()
                    if (sourceStack.isEmpty) break@all
                }
            }
        }
    }

}
