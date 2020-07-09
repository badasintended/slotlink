package badasintended.slotlink.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import badasintended.spinnery.widget.WSlot

class MasterScreenHandler(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) :
    ModScreenHandler(syncId, player) {

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
