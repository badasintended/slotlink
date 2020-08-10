package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.common.util.chat
import badasintended.slotlink.common.util.toTag
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class MasterBlock : ModBlock("master"), BlockEntityProvider {

    override fun createBlockEntity(view: BlockView): BlockEntity = MasterBlockEntity()

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        val blockEntity = world.getBlockEntity(pos)
        val nbt = blockEntity!!.toTag(CompoundTag())

        nbt.put("storagePos", ListTag())
        blockEntity.fromTag(state, nbt)
        blockEntity.markDirty()
    }

    override fun neighborUpdate(
        state: BlockState, world: World, pos: BlockPos, block: Block, neighborPos: BlockPos, moved: Boolean
    ) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved)

        val neighborState = world.getBlockState(neighborPos)
        val neighborBlock = neighborState.block

        if (neighborBlock is ChildBlock) {
            val neighborBlockEntity = world.getBlockEntity(neighborPos)
            val neighborNbt = neighborBlockEntity!!.toTag(CompoundTag())
            val neighborHasMaster = neighborNbt.getBoolean("hasMaster")
            if (!neighborHasMaster) {
                val masterPos = pos.toTag()
                neighborNbt.put("masterPos", masterPos)
                neighborNbt.putBoolean("hasMaster", true)
                neighborBlockEntity.fromTag(neighborState, neighborNbt)
                neighborBlockEntity.markDirty()
                world.updateNeighbors(neighborPos, neighborBlock)
            }
        }
    }

    override fun onUse(
        state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)!! as MasterBlockEntity
            val inventories = blockEntity.getLinkedInventories(world).keys

            player.chat("")
            player.chat("$translationKey.use1", pos.x, pos.y, pos.z)
            player.chat("$translationKey.use2", inventories.size)
            player.chat("$translationKey.use3", inventories.stream().mapToInt { it.size() }.sum())
            player.chat("$translationKey.use4", inventories.stream().mapToInt { it.size() * it.maxCountPerStack }.sum())
        }
        return ActionResult.SUCCESS
    }

}
