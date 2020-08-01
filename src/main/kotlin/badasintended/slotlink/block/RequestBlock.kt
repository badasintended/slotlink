package badasintended.slotlink.block

import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.common.util.*
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class RequestBlock : ChildBlock("request") {

    override fun createBlockEntity(view: BlockView): BlockEntity = RequestBlockEntity()

    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        val blockEntity = world.getBlockEntity(pos) as RequestBlockEntity
        if (!blockEntity.hasMaster) player.actionBar("${translationKey}.hasNoMaster")
        else if (!world.isClient) {
            player.openScreen("request") { buf ->
                buf.writeBlockPos(pos)
                buf.writeInt(blockEntity.lastSort)
                buf.writeIdentifier(world.registryKey.value)
                buf.writeBlockPos(blockEntity.masterPos.toPos())
            }
        }

        return ActionResult.SUCCESS
    }

}
