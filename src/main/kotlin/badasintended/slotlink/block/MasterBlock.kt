package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.util.chat
import badasintended.slotlink.util.toNbt
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MasterBlock : ModBlock("master"), BlockEntityProvider {

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = MasterBlockEntity(pos, state)

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        val blockEntity = world.getBlockEntity(pos)!!
        val nbt = blockEntity.writeNbt(NbtCompound())

        nbt.put("storagePos", NbtList())
        blockEntity.readNbt(nbt)
        blockEntity.markDirty()
    }

    @Suppress("DEPRECATION")
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
            val neighborBlockEntity = world.getBlockEntity(neighborPos)!!
            val neighborNbt = neighborBlockEntity.writeNbt(NbtCompound())
            val neighborHasMaster = neighborNbt.getBoolean("hasMaster")
            if (!neighborHasMaster) {
                val masterPos = pos.toNbt()
                neighborNbt.put("masterPos", masterPos)
                neighborNbt.putBoolean("hasMaster", true)
                neighborBlockEntity.readNbt(neighborNbt)
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
        if (!world.isClient) {
            val blockEntity = world.getBlockEntity(pos)!! as MasterBlockEntity
            val inventories = blockEntity.getInventories(world, true)

            player.chat("")
            player.chat("$translationKey.use1", pos.x, pos.y, pos.z)
            player.chat("$translationKey.use2", inventories.size)
            player.chat(
                "$translationKey.use3",
                inventories.sumOf { it.size() },
                inventories.sumOf { inv -> (0 until inv.size()).filter { inv.getStack(it).isEmpty }.size }
            )
            player.chat("$translationKey.use4", inventories.sumOf { it.size() * it.maxCountPerStack })
        }
        return ActionResult.SUCCESS
    }

}
