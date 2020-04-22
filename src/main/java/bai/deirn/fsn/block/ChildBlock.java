package bai.deirn.fsn.block;

import bai.deirn.fsn.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ChildBlock extends FSNBlock implements BlockEntityProvider {

    public ChildBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        CompoundTag nbt = NbtHelper.fromBlockPos(pos);

        Utils.getPosAround(pos).forEach(relativePos -> {
            Block relativeBlock = world.getBlockState(relativePos).getBlock();

            if (relativeBlock.hasBlockEntity() && relativeBlock instanceof ChildBlock) {
                CompoundTag relativeNbt = NbtHelper.fromBlockPos(relativePos);
                relativeNbt.getKeys().forEach(key -> {
                    if (nbt.contains(key)) {
                        nbt.put(key, relativeNbt.get(key));
                    }
                });
                assert blockEntity != null;
                blockEntity.fromTag(nbt);
                blockEntity.markDirty();
                return;
            }
        });
    }

}
