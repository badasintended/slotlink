package badasintended.slotlink.block

import badasintended.slotlink.block.entity.TransferCableBlockEntity
import badasintended.slotlink.common.registry.NetworkRegistry
import badasintended.slotlink.common.util.buf
import badasintended.slotlink.common.util.openScreen
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import kotlin.reflect.KClass

abstract class TransferCableBlock(id: String, blockEntity: KClass<out TransferCableBlockEntity>) :
    ConnectorCableBlock(id, blockEntity) {

    override fun WorldAccess.isBlockIgnored(block: Block) = block is ModBlock

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player.mainHandStack.isEmpty) {
            if (!world.isClient) {
                player.openScreen("transfer") { it.writeBlockPos(pos) }
                val blockEntity = world.getBlockEntity(pos)
                if (blockEntity is TransferCableBlockEntity) {
                    val buf = buf()
                    buf.writeInt(blockEntity.side.id)
                    buf.writeBoolean(blockEntity.isBlackList)
                    blockEntity.filter.forEach { buf.writeItemStack(it) }
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, NetworkRegistry.TRANSFER_READ, buf)
                }
            }
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

}
