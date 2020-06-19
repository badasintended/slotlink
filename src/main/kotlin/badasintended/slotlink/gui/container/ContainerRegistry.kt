package badasintended.slotlink.gui.container

import badasintended.slotlink.block.BlockRegistry
import badasintended.slotlink.block.ModBlock
import net.fabricmc.fabric.api.container.ContainerFactory
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.minecraft.container.Container
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

object ContainerRegistry {

    fun init() {
        r(BlockRegistry.REQUEST) { id, _, player, buf -> RequestContainer(id, player, buf) }
        r(BlockRegistry.MASTER) { id, _, player, buf -> MasterContainer(id, player, buf) }
    }

    private fun <C : Container> r(modBlock: ModBlock, function: (Int, Identifier, PlayerEntity, PacketByteBuf) -> C) {
        ContainerProviderRegistry.INSTANCE.registerFactory(modBlock.id, ContainerFactory(function))
    }

}
