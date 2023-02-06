package badasintended.slotlink.item

import badasintended.slotlink.network.Network
import badasintended.slotlink.util.actionBar
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class MultiDimRemoteItem(id: String = "multi_dim_remote") : RemoteItem(id) {

    override val level = 1

    override fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        remoteSlot: Int,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    ) {
        if (!world.isClient) {
            val dim = world.server!!.getWorld(masterDim)
            if (dim == null) {
                player.actionBar("${baseTlKey}.invalidDim")
            } else {
                val network = Network.get(dim, masterPos)
                if (network == null || network.deleted) {
                    player.actionBar("${baseTlKey}.masterNotFound")
                } else {
                    network.master?.also {
                        player.openHandledScreen(ScreenHandlerFactory(dim, it, remoteSlot))
                    }
                }
            }
        }
    }

}
