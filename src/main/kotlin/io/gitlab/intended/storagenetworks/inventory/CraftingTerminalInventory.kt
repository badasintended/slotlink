package io.gitlab.intended.storagenetworks.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import spinnery.common.BaseInventory
import spinnery.widget.WSlot

class CraftingTerminalInventory(
    syncId: Int,
    player: PlayerEntity,
    name: Text
) : ModInventory(syncId, player, name){

    companion object {
        const val INVENTORY = 1
    }

    init {
        val mainInterface = `interface`
        val inventory = BaseInventory(27)

        inventories[INVENTORY] = inventory

        mainInterface.createChild { WSlot() }
        WSlot.addHeadlessArray(mainInterface, 0, INVENTORY, 9, 3)
        WSlot.addHeadlessPlayerInventory(mainInterface)
    }

}