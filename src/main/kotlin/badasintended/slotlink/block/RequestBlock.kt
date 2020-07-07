package badasintended.slotlink.block

import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.common.openScreen
import badasintended.slotlink.common.sendActionBar
import badasintended.slotlink.common.tag2Pos
import badasintended.slotlink.common.writeRequestData
import badasintended.slotlink.network.NetworkRegistry
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class RequestBlock : ChildBlock("request") {

    override fun createBlockEntity(view: BlockView): BlockEntity = RequestBlockEntity()

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        val blockEntity = world.getBlockEntity(pos)!!
        val nbt = blockEntity.toTag(CompoundTag())
        val hasMaster = nbt.getBoolean("hasMaster")
        if (!hasMaster) sendActionBar(world, player, "${translationKey}.hasNoMaster")
        else if (!world.isClient) {
            openScreen("request", player) { buf ->
                buf.writeBlockPos(pos)
                buf.writeInt(nbt.getInt("lastSort"))
                val masterPos = tag2Pos(nbt.getCompound("masterPos"))
                writeRequestData(buf, world, masterPos)
            }

            ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                player, NetworkRegistry.FIRST_SORT, PacketByteBuf(Unpooled.buffer())
            )
        }


        return ActionResult.SUCCESS
    }

}
