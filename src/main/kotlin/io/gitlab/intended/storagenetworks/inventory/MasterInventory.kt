package io.gitlab.intended.storagenetworks.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

class MasterInventory(
    syncId: Int,
    player: PlayerEntity,
    name: Text
) : ModInventory(syncId, player, name) {

    companion object {
        const val INV = 2
    }

}