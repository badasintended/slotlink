package io.gitlab.intended.storagenetworks.container

import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import spinnery.common.BaseInventory
import spinnery.widget.WSlot

class RequestContainer(
    syncId: Int,
    player: PlayerEntity
) : ModContainer(syncId, player) {

    companion object {
        const val INV = 1
    }

    val name = TranslatableText(ModBlocks.REQUEST.translationKey)

    init {
        val root = `interface`
        val inventory = BaseInventory(27)

        inventories[INV] = inventory

        root.createChild { WSlot() }
        WSlot.addHeadlessArray(root, 0, INV, 9, 3)
        WSlot.addHeadlessPlayerInventory(root)
    }

}
