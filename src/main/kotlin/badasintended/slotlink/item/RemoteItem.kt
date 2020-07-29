package badasintended.slotlink.item

import badasintended.slotlink.common.actionBar
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class MultiDimRemoteItem : AbstractRemoteItem("multi_dim_remote")

class UnlimitedRemoteItem : AbstractRemoteItem("unlimited_remote") {

    override fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        hand: Hand,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    ) {
        if (world.registryKey != masterDim) {
            player.actionBar("${baseTlKey}.differentDimension")
        } else super.use(world, player, stack, hand, masterPos, masterDim)
    }

}

class LimitedRemoteItem : AbstractRemoteItem("limited_remote") {

    override fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        hand: Hand,
        masterPos: BlockPos,
        masterDim: RegistryKey<World>
    ) {
        if (player.pos.distanceTo(Vec3d.of(masterPos)) > 512) {
            player.actionBar("${baseTlKey}.tooFarFromMaster")
        } else super.use(world, player, stack, hand, masterPos, masterDim)
    }

}
