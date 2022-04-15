package badasintended.slotlink.mixin;

import badasintended.slotlink.block.TransferCableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallMountedBlock.class)
public abstract class WallMountedBlockMixin {

    @Shadow
    protected static Direction getDirection(BlockState state) {
        throw new AssertionError();
    }

    @Inject(at = @At("RETURN"), method = "canPlaceAt(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", cancellable = true)
    private void slotlink$placeLeverAtCable(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block wanted = state.getBlock();
        Block on = world.getBlockState(pos.offset(getDirection(state).getOpposite())).getBlock();
        if (wanted instanceof LeverBlock && on instanceof TransferCableBlock) {
            cir.setReturnValue(true);
        }
    }

}
