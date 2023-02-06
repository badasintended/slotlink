package badasintended.slotlink.item

import badasintended.slotlink.util.actionBar
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class LimitedRemoteItem : UnlimitedRemoteItem("limited_remote") {

    override val level = 3

    override fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        remoteSlot: Int,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    ) {
        if (player.pos.distanceTo(Vec3d.of(masterPos)) > 512) {
            player.actionBar("${baseTlKey}.tooFarFromMaster")
        } else super.use(world, player, stack, remoteSlot, masterPos, masterDim)
    }

}
