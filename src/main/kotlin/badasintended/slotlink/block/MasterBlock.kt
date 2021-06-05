package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.Connection
import badasintended.slotlink.network.Network
import badasintended.slotlink.util.chat
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
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
import net.minecraft.world.WorldAccess

class MasterBlock : ModBlock("master") {

    override fun createBlockEntity(pos: BlockPos, state: BlockState) = MasterBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return checkType(type, BlockEntityTypes.MASTER, MasterBlockEntity.Ticker)
    }

    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
        super.onPlaced(world, pos, state, placer, itemStack)

        val blockEntity = world.getBlockEntity(pos)!!
        val nbt = blockEntity.writeNbt(NbtCompound())

        nbt.put("storagePos", NbtList())
        blockEntity.readNbt(nbt)
        blockEntity.markDirty()
    }

    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        val neighborState = world.getBlockState(neighborPos)
        val neighborBlock = neighborState.block

        if (neighborBlock is ChildBlock) {
            val connection = world.getBlockEntity(neighborPos) as? Connection
            connection?.also {
                val master = world.getBlockEntity(pos) as MasterBlockEntity
                if (it.connect(master)) {
                    world.updateNeighbors(neighborPos, neighborBlock)
                }
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

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        super.onBroken(world, pos, state)

        if (world is World) {
            Network.get(world, pos)?.delete()
        }
    }

}
