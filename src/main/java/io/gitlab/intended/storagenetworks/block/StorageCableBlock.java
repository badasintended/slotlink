package io.gitlab.intended.storagenetworks.block;

import io.gitlab.intended.storagenetworks.block.entity.StorageCableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StorageCableBlock extends ConnectorCableBlock {

    public StorageCableBlock(String id) {
        super(id);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new StorageCableBlockEntity();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        CompoundTag masterPos = world.getBlockEntity(pos).toTag(new CompoundTag()).getCompound("masterPos");
        BlockEntity master = world.getBlockEntity(NbtHelper.toBlockPos(masterPos));
    }

}
