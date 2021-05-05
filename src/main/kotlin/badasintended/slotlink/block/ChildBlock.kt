package badasintended.slotlink.block

import badasintended.slotlink.block.entity.ChildBlockEntity
import badasintended.slotlink.block.entity.MasterBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

abstract class ChildBlock(id: String, private val blockEntity: () -> BlockEntity, settings: Settings = SETTINGS) :
    ModBlock(id, settings), BlockEntityProvider {

    // TODO: Optimize this part
    override fun neighborUpdate(
        state: BlockState,
        world: World,
        pos: BlockPos,
        block: Block,
        neighborPos: BlockPos,
        moved: Boolean
    ) {
        val blockEntity = world.getBlockEntity(pos) as ChildBlockEntity
        val neighborBlock = world.getBlockState(neighborPos).block
        val neighborBlockEntity = world.getBlockEntity(neighborPos)
        val currentlyHasMaster = blockEntity.hasMaster

        if (neighborBlockEntity is ChildBlockEntity) {
            val neighborHasMaster = neighborBlockEntity.hasMaster

            val currentMasterPos = blockEntity.masterPos
            val neighborMasterPos = neighborBlockEntity.masterPos

            if (currentlyHasMaster and !neighborHasMaster) {
                if (currentMasterPos == neighborMasterPos) {
                    blockEntity.hasMaster = false
                    blockEntity.markDirty()
                    world.updateNeighbors(pos, block)
                } else {
                    neighborBlockEntity.hasMaster = true
                    neighborBlockEntity.masterPos = currentMasterPos
                    neighborBlockEntity.markDirty()
                    world.updateNeighbors(neighborPos, neighborBlock)
                }
            } else if (!currentlyHasMaster and neighborHasMaster) {
                blockEntity.hasMaster = true
                blockEntity.masterPos = neighborMasterPos
                blockEntity.markDirty()
                world.updateNeighbors(pos, block)
            }
        } else if (neighborBlockEntity is MasterBlockEntity) {
            if (!currentlyHasMaster) {
                blockEntity.masterPos = neighborPos
                blockEntity.hasMaster = true
                blockEntity.markDirty()
                world.updateNeighbors(pos, block)
            }
        } else if (currentlyHasMaster) {
            blockEntity.hasMaster = false
            blockEntity.markDirty()
            world.updateNeighbors(pos, block)
        }
    }

    override fun createBlockEntity(world: BlockView) = blockEntity.invoke()

    override fun appendTooltip(
        stack: ItemStack,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(TranslatableText("block.slotlink.child.tooltip").formatted(Formatting.GRAY))
    }

}
