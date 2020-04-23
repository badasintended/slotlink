package bai.deirn.fsn.block;

import bai.deirn.fsn.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class ChildBlock extends FSNBlock implements BlockEntityProvider {

    public ChildBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return null;
    }

    private void validateMaster(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        CompoundTag nbt = blockEntity.toTag(new CompoundTag());
        if (nbt.getBoolean("hasMaster")) {
            CompoundTag master = nbt.getCompound("masterPos");
            BlockPos masterPos = new BlockPos(master.getInt("x"), master.getInt("y"), master.getInt("z"));
            Utils.LOGGER.warning(masterPos.toShortString());
            if (!(world.getBlockState(masterPos).getBlock() instanceof MasterBlock)) {
                nbt.putBoolean("hasMaster", false);
                blockEntity.fromTag(nbt);
                blockEntity.markDirty();
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        /*
        Utils.getPosAround(pos).forEach(neighborPos -> {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            CompoundTag currentNbt = blockEntity.toTag(new CompoundTag());
            Block neighborBlock = world.getBlockState(neighborPos).getBlock();

            CompoundTag neighborMasterPos = new CompoundTag();
            boolean neighborHasMaster = false;

            if (neighborBlock instanceof ChildBlock) {
                CompoundTag neighborNbt = world.getBlockEntity(neighborPos).toTag(new CompoundTag());
                neighborMasterPos = neighborNbt.getCompound("masterPos");
                neighborHasMaster = neighborNbt.getBoolean("hasMaster");
            } else if (neighborBlock instanceof MasterBlock) {
                neighborMasterPos.putInt("x", neighborPos.getX());
                neighborMasterPos.putInt("y", neighborPos.getY());
                neighborMasterPos.putInt("z", neighborPos.getZ());
            } else return;
            if (!currentNbt.getBoolean("hasMaster")) {
                currentNbt.put("masterPos", neighborMasterPos);
                currentNbt.putBoolean("hasMaster", neighborHasMaster);
                blockEntity.fromTag(currentNbt);
                blockEntity.markDirty();
            }
        });
         */
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        CompoundTag currentNbt = blockEntity.toTag(new CompoundTag());
        Block neighborBlock = neighborState.getBlock();

        boolean currentlyHasMaster = currentNbt.getBoolean("hasMaster");

        if (currentlyHasMaster) {
            CompoundTag currentMasterPos = currentNbt.getCompound("masterPos");
            if (neighborBlock instanceof ChildBlock) {
                BlockEntity neighborBlockEntity = world.getBlockEntity(neighborPos);
                CompoundTag neighborNbt = neighborBlockEntity.toTag(new CompoundTag());
                boolean neighborHasMaster = neighborNbt.getBoolean("hasMaster");
                if (!neighborHasMaster) {
                    CompoundTag neighborMasterPos = neighborNbt.getCompound("masterPos");
                    if (neighborMasterPos.equals(currentMasterPos)) {
                        currentNbt.putBoolean("hasMaster", false);
                        blockEntity.fromTag(currentNbt);
                        blockEntity.markDirty();
                    } else {
                        neighborNbt.put("masterPos", currentMasterPos);
                        neighborNbt.putBoolean("hasMaster", true);
                        neighborBlockEntity.fromTag(neighborNbt);
                        neighborBlockEntity.markDirty();
                    }
                }
            } else if (!(neighborBlock instanceof MasterBlock)) {
                boolean saved = false;
                for (BlockPos posAround : Utils.getPosAround(pos)) {
                    Block blockAround = world.getBlockState(posAround).getBlock();
                    if ((blockAround instanceof ChildBlock) || (blockAround instanceof MasterBlock)) {
                        saved = true;
                    }
                }
                if (!saved) {
                    currentNbt.putBoolean("hasMaster", false);
                    blockEntity.fromTag(currentNbt);
                    blockEntity.markDirty();
                }
            }
        } else {
            if (neighborBlock instanceof ChildBlock) {
                BlockEntity neighborBlockEntity = world.getBlockEntity(neighborPos);
                blockEntity.fromTag(neighborBlockEntity.toTag(new CompoundTag()));
                blockEntity.markDirty();
            } else if (neighborBlock instanceof MasterBlock) {
                CompoundTag masterPos = new CompoundTag();
                masterPos.putInt("x", neighborPos.getX());
                masterPos.putInt("y", neighborPos.getY());
                masterPos.putInt("z", neighborPos.getZ());
                currentNbt.put("masterPos", masterPos);
                currentNbt.putBoolean("hasMaster", true);
                blockEntity.fromTag(currentNbt);
                blockEntity.markDirty();
            }
        }

        return state;
    }

}
