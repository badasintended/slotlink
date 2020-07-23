package badasintended.slotlink.item

import badasintended.slotlink.common.sendActionBar
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType

class UnlimitedRemoteItem : AbstractRemoteItem("unlimited_remote")

class LimitedRemoteItem : AbstractRemoteItem("limited_remote") {

    override fun use(
        world: World,
        player: PlayerEntity,
        stack: ItemStack,
        hand: Hand,
        masterPos: BlockPos,
        masterDim: RegistryKey<DimensionType>?
    ) {
        sendActionBar(world, player, "${player.pos.distanceTo(Vec3d.of(masterPos))}")
        if (player.pos.distanceTo(Vec3d.of(masterPos)) > 512) {
            sendActionBar(world, player, "${baseTlKey}.tooFarFromMaster")
        } else super.use(world, player, stack, hand, masterPos, masterDim)
    }

}
