package io.gitlab.intended.storagenetworks.container

import io.gitlab.intended.storagenetworks.block.ModBlocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText

class MasterContainer(
    syncId: Int,
    player: PlayerEntity
) : ModContainer(syncId, player) {

    companion object {
        const val INV = 2
    }

    val name = TranslatableText(ModBlocks.MASTER.translationKey)

}
