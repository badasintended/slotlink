package badasintended.slotlink.block.entity

import badasintended.slotlink.registry.BlockEntityTypeRegistry
import badasintended.slotlink.util.*
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ImportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypeRegistry.IMPORT_CABLE) {

    override var side = Direction.DOWN

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val source = getLinkedInventory(world)?.first ?: return false
        var sourceSlot = -1
        var sourceStack = ItemStack.EMPTY

        var targets = master.getLinkedInventories(world)

        val sourceSlots = if (source is SidedInventory) source
            .getAvailableSlots(side)
            .toList()
            .filter { source.canExtract(it, source.getStack(it), side) }
        else (0 until source.size()).toList()

        for (i in sourceSlots) {
            val stack = source.getStack(i)
            if (stack.isValid()) {
                val filtered = targets.filter r@{ entry ->
                    val filter = entry.value
                    val isBlackList = filter.first
                    if (filter.second.isEmpty()) return@r true
                    return@r (filter.second.contains(stack.item)) == !isBlackList
                }
                if (filtered.isNotEmpty()) {
                    sourceSlot = i
                    sourceStack = stack
                    targets = filtered
                    break
                }
            }
        }

        if (sourceSlot == -1) return false

        for (target in targets.keys) {
            for (j in 0 until target.size()) {
                target.mergeStack(j, sourceStack, side)
                if (sourceStack.isEmpty) break
            }
            target.markDirty()
            if (sourceStack.isEmpty) break
        }

        source.setStack(sourceSlot, sourceStack)
        source.markDirty()
        return sourceStack.isEmpty
    }

    override fun markDirty() {
        super.markDirty()

        if (hasMaster) {
            val master = world?.getBlockEntity(masterPos.toPos())

            if (master is MasterBlockEntity) {
                master.importCables.add(pos.toTag())
                master.markDirty()
            }
        }
    }

}
