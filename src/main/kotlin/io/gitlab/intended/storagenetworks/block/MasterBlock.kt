package io.gitlab.intended.storagenetworks.block

import io.gitlab.intended.storagenetworks.block.entity.MasterBlockEntity
import io.gitlab.intended.storagenetworks.container.ModContainer
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class MasterBlock(id: String) : ModBlock(id), BlockEntityProvider {

    override fun createBlockEntity(view: BlockView): BlockEntity = MasterBlockEntity()

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        super.neighborUpdate(state, world, pos, block, neighborPos, moved)

        val neighborState = world.getBlockState(neighborPos)
        val neighborBlock = neighborState.block

        if (neighborBlock is ChildBlock) {
            val neighborBlockEntity = world.getBlockEntity(neighborPos)
            val neighborNbt = neighborBlockEntity!!.toTag(CompoundTag())
            val neighborHasMaster = neighborNbt.getBoolean("hasMaster")
            if (!neighborHasMaster) {
                val masterPos = CompoundTag()
                masterPos.putInt("x", pos.x)
                masterPos.putInt("y", pos.y)
                masterPos.putInt("z", pos.z)
                neighborNbt.put("masterPos", masterPos)
                neighborNbt.putBoolean("hasMaster", true)
                neighborBlockEntity.fromTag(neighborNbt)
                neighborBlockEntity.markDirty()
                world.updateNeighbors(neighborPos, neighborBlock)
            }
        }
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) ModContainer.open(this, player)
        return ActionResult.SUCCESS
    }

}
