package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.toPos
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import spinnery.common.utility.StackUtilities

class ImportCableBlockEntity : TransferCableBlockEntity(BlockEntityTypeRegistry.IMPORT_CABLE) {

    override var side = Direction.UP

    override fun transfer(world: World, master: MasterBlockEntity) {
        val source = getLinkedInventory(world) ?: return
        var sourceSlot = -1
        var sourceStack = ItemStack.EMPTY

        val sourceSlots =
            if (source is SidedInventory) source.getAvailableSlots(side.opposite).toList()
            else (0 until source.size()).toList()

        for (i in sourceSlots) {
            val stack = source.getStack(i)
            if (stack.isValid()) {
                sourceSlot = i
                sourceStack = stack
                break
            }
        }

        if (sourceSlot == -1) return

        for (tag in master.linkCables) {
            val linkCablePos = (tag as CompoundTag).toPos()
            val linkCable = world.getBlockEntity(linkCablePos) ?: continue
            if (linkCable !is LinkCableBlockEntity) continue
            val target = linkCable.getLinkedInventory(world) ?: continue
            for (j in 0 until target.size()) {
                val targetStack = target.getStack(j)
                StackUtilities.merge(sourceStack, targetStack, sourceStack.maxCount, targetStack.maxCount)
                    .apply({ sourceStack = it }, { target.setStack(j, it) })
                if (sourceStack.isEmpty) break
            }
            target.markDirty()
            if (sourceStack.isEmpty) break
        }

        source.setStack(sourceSlot, sourceStack)
        source.markDirty()
    }

}
