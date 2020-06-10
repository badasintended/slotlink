package io.gitlab.intended.storagenetworks.gui.container

import net.minecraft.container.BlockContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.PacketByteBuf
import spinnery.common.container.BaseContainer

abstract class ModContainer(
    syncId: Int,
    val player: PlayerEntity,
    buf: PacketByteBuf,
    val context: BlockContext = BlockContext.create(player.world, buf.readBlockPos())
) : BaseContainer(syncId, player.inventory)
