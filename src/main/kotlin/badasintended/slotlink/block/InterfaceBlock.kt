package badasintended.slotlink.block

import badasintended.slotlink.block.entity.InterfaceBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

class InterfaceBlock : ChildBlock("interface", ::InterfaceBlockEntity) {

    override fun appendTooltip(
        stack: ItemStack,
        world: BlockView?,
        tooltip: MutableList<Text>,
        options: TooltipContext
    ) {
        super.appendTooltip(stack, world, tooltip, options)
        tooltip.add(Text.translatable("block.slotlink.filter.tooltip").formatted(Formatting.GRAY))
        tooltip.add(Text.translatable("block.slotlink.interface.tooltip").formatted(Formatting.GRAY))
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (player.mainHandStack.isEmpty) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }

}