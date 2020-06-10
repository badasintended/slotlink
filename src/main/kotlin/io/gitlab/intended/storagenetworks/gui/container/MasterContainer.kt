package io.gitlab.intended.storagenetworks.gui.container

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.PacketByteBuf

class MasterContainer(syncId: Int, player: PlayerEntity, buf: PacketByteBuf) : ModContainer(syncId, player, buf) {

    init { }

}
