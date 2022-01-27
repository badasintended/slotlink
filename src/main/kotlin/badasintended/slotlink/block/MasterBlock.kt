package badasintended.slotlink.block

import badasintended.slotlink.block.entity.MasterBlockEntity
import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.Network
import badasintended.slotlink.network.Node
import badasintended.slotlink.util.actionBar
import net.fabricmc.fabric.api.block.BlockAttackInteractionAware
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

class MasterBlock : ModBlock("master"), BlockAttackInteractionAware {

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
        val nbt = blockEntity.createNbt()

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
            val node = world.getBlockEntity(neighborPos) as? Node
            node?.also {
                val master = world.getBlockEntity(pos) as MasterBlockEntity
                if (it.connect(master)) {
                    world.updateNeighbors(neighborPos, neighborBlock)
                }
            }
        }
    }

    override fun onBroken(world: WorldAccess, pos: BlockPos, state: BlockState) {
        super.onBroken(world, pos, state)

        if (world is World) {
            Network.get(world, pos)?.delete()
        }
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(TranslatableText("block.slotlink.master.tooltip").formatted(Formatting.GRAY))
    }

    override fun onAttackInteraction(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        direction: Direction
    ): Boolean {
        if (!player.isSpectator && player.isSneaking && player.getStackInHand(hand).isEmpty) {
            Network.get(world, pos)?.validate()
            if (!player.isCreative) player.playSound(SoundEvents.BLOCK_STONE_BREAK, 1.0f, 1.0f)
            player.actionBar("block.slotlink.master.revalidated")
            return !world.isClient
        }
        return false
    }

}
