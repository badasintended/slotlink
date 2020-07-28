package badasintended.slotlink.block

import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.common.actionBar
import badasintended.slotlink.common.openScreen
import badasintended.slotlink.common.toPos
import badasintended.slotlink.common.writeReqData
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
        if (!hasMaster) player.actionBar("${translationKey}.hasNoMaster")
        else if (!world.isClient) {
            openScreen("request", player) { buf ->
                buf.writeBlockPos(pos)
                buf.writeInt(nbt.getInt("lastSort"))
                val masterPos = nbt.getCompound("masterPos").toPos()
                buf.writeReqData(world, masterPos)
            }
        }


        return ActionResult.SUCCESS
    }

}
