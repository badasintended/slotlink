package io.gitlab.intended.storagenetworks.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import spinnery.common.BaseInventory
import spinnery.widget.WSlot

class CraftingTerminalInventory(
    syncId: Int,
    player: PlayerEntity,
    name: Text
) : ModInventory(syncId, player, name) {

    companion object {
        const val INV = 1
    }

    init {
        val root = `interface`
        val inventory = BaseInventory(27)

        inventories[INV] = inventory

        root.createChild { WSlot() }
        WSlot.addHeadlessArray(root, 0, INV, 9, 3)
        WSlot.addHeadlessPlayerInventory(root)
    }

}