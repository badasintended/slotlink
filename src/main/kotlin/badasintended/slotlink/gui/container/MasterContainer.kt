package badasintended.slotlink.gui.container

import badasintended.slotlink.block.BlockRegistry.MASTER
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.PacketByteBuf
import spinnery.widget.WSlot

class MasterContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModContainer(MASTER, syncId, player, buf) {

    val linkCableCount = buf.readInt()
    val totalSlot = buf.readInt()
    val maxCount = buf.readInt()
    val totalInv = buf.readInt()

    init {
        val stacks = arrayListOf<ItemStack>()
        for (i in 0 until totalInv) stacks.add(buf.readItemStack())
        stacks.sortByDescending { it.count }

        inventories[1] = CraftingInventory(this, totalInv, 1)

        stacks.forEachIndexed { index, stack ->
            val slot = root.createChild { WSlot() }
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(index)
            slot.setStack<WSlot>(stack)
            slot.setLocked<WSlot>(true)
        }
    }

}
