package badasintended.slotlink.item

import badasintended.slotlink.block.MasterBlock
import badasintended.slotlink.common.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Hand.MAIN_HAND
import net.minecraft.util.Hand.OFF_HAND
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.dimension.DimensionType

class RemoteItem(id: String = "remote_unli") : ModItem(id) {

    override fun use(world: World, player: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = when (hand) {
            MAIN_HAND -> player.mainHandStack
            OFF_HAND -> player.offHandStack
        }

        val masterPosTag = stack.orCreateTag.getCompound("masterPos")
        val masterDim = DimensionType.byRawId(stack.orCreateTag.getInt("masterDim"))

        if (masterPosTag == CompoundTag()) {
            addClientMessage(world, player, "${translationKey}.hasNoMaster")
        } else if (masterDim == null) {
            addClientMessage(world, player, "${translationKey}.invalidMasterDim")
        } else if (masterDim != world.dimension.type) {
            // yeah i cant make it work across dimension
            addClientMessage(world, player, "${translationKey}.differentDim")
        } else {
            if (!world.isClient) {
                openScreen("remote", player) { buf ->
                    buf.writeBlockPos(BlockPos(player.pos))
                    buf.writeInt(stack.orCreateTag.getInt("lastSort"))
                    val masterPos = tag2Pos(masterPosTag)
                    writeRequestData(buf, world.server, masterDim, masterPos)
                    buf.writeBoolean(hand == OFF_HAND)
                }
            }
        }

        return super.use(world, player, hand)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val player = context.player
        val stack = context.stack
        val world = context.world
        val pos = context.blockPos
        val dimId = world.dimension.type.rawId

        val block = world.getBlockState(pos).block
        if (block is MasterBlock) {
            if (player != null) if (player.isSneaking) {
                stack.orCreateTag.put("masterPos", pos2Tag(pos))
                stack.orCreateTag.putInt("masterDim", dimId)
                addClientMessage(world, player, "${translationKey}.linked", pos.x, pos.y, pos.z, dimId)
                return ActionResult.CONSUME
            }
        }

        return ActionResult.PASS
    }

}
