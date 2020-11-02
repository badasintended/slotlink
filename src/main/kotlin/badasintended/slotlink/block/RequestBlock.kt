package badasintended.slotlink.block

import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.util.actionBar
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class RequestBlock : ChildBlock("request", ::RequestBlockEntity) {

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val blockEntity = world.getBlockEntity(pos) as RequestBlockEntity
        if (!blockEntity.hasMaster) {
            player.actionBar("${translationKey}.hasNoMaster")
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        }
        return ActionResult.SUCCESS
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos) ?: return null
        if (blockEntity !is RequestBlockEntity) return null
        return blockEntity
    }

}
