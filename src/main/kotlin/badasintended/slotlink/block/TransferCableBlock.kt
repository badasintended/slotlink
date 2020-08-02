package badasintended.slotlink.block

import badasintended.slotlink.block.entity.TransferCableBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import kotlin.reflect.KClass

abstract class TransferCableBlock(id: String, blockEntity: KClass<out TransferCableBlockEntity>) : ConnectorCableBlock(
    id, blockEntity
) {

    override fun WorldAccess.isBlockIgnored(block: Block) = block is ModBlock

    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        if (player.mainHandStack.isEmpty) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

    override fun createScreenHandlerFactory(
        state: BlockState, world: World, pos: BlockPos
    ): NamedScreenHandlerFactory? {
        val blockEntity = world.getBlockEntity(pos) ?: return null
        if (blockEntity !is TransferCableBlockEntity) return null
        return blockEntity
    }

}
