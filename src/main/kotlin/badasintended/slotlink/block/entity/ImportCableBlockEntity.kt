package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class ImportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypes.IMPORT_CABLE) {

    override var side = Direction.DOWN

    override fun transferInternal(world: World, master: MasterBlockEntity): Boolean {
        val source = getInventory(world)
        if (source.isNull) return false
        val sourceSlots = source.getAvailableSlots(side).filter { source.canExtract(it, source.getStack(it), side) }

        var sourceSlot = -1
        var sourceStack = ItemStack.EMPTY

        val targets = master.getInventories(world)

        for (slot in sourceSlots) {
            val stack = source.getStack(slot)
            if (stack.isEmpty) continue
            if (source.isValid(stack)) {
                if (targets.any { it.isValid(stack) }) {
                    sourceStack = stack
                    sourceSlot = slot
                    break
                }
            }
        }

        if (sourceStack.isEmpty || sourceSlot == -1) return false

        for (target in targets) {
            for (i in 0 until target.size()) {
                target.merge(i, sourceStack, side)
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
            val master = world?.getBlockEntity(masterPos)

            if (master is MasterBlockEntity) {
                master.importPos.add(pos)
                master.markDirty()
            }
        }
    }

}
