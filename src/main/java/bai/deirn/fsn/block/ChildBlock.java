package bai.deirn.fsn.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
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
            if (!(world.getBlockState(masterPos).getBlock() instanceof MasterBlock)) {
                nbt.putBoolean("hasMaster", false);
                blockEntity.fromTag(nbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        }
    }

    /*
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
                    Utils.LOGGER.warning(currentMasterPos.toString()+" "+neighborMasterPos.toString());
                    if (currentMasterPos.toString().equals(neighborMasterPos.toString())) {
                        currentNbt.putBoolean("hasMaster", false);
                        blockEntity.fromTag(currentNbt);
                        blockEntity.markDirty();
                        world.updateNeighbors(pos, state.getBlock());
                    } else {
                        neighborNbt.put("masterPos", currentMasterPos);
                        neighborNbt.putBoolean("hasMaster", true);
                        neighborBlockEntity.fromTag(neighborNbt);
                        neighborBlockEntity.markDirty();
                        world.updateNeighbors(neighborPos, neighborState.getBlock());
                    }
                }
            } else if (!(neighborBlock instanceof MasterBlock)) {
                BlockPos masterPos = new BlockPos(currentMasterPos.getInt("x"), currentMasterPos.getInt("y"), currentMasterPos.getInt("z"));
                Block masterBlock = world.getBlockState(masterPos).getBlock();
                if (!(masterBlock instanceof MasterBlock)) {
                    currentNbt.putBoolean("hasMaster", false);
                    blockEntity.fromTag(currentNbt);
                    blockEntity.markDirty();
                    world.updateNeighbors(pos, state.getBlock());
                }
            }
        }
        if (!currentlyHasMaster) {
            if (neighborBlock instanceof ChildBlock) {
                BlockEntity neighborBlockEntity = world.getBlockEntity(neighborPos);
                CompoundTag neighborNbt = neighborBlockEntity.toTag(new CompoundTag());
                blockEntity.fromTag(neighborNbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, state.getBlock());
            } else if (neighborBlock instanceof MasterBlock) {
                CompoundTag masterPos = new CompoundTag();
                masterPos.putInt("x", neighborPos.getX());
                masterPos.putInt("y", neighborPos.getY());
                masterPos.putInt("z", neighborPos.getZ());
                currentNbt.put("masterPos", masterPos);
                currentNbt.putBoolean("hasMaster", true);
                blockEntity.fromTag(currentNbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, state.getBlock());
            }
        }

        return state;
    }
     */

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos, boolean moved) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState neighborState = world.getBlockState(neighborPos);
        Block neighborBlock = neighborState.getBlock();

        CompoundTag currentNbt = blockEntity.toTag(new CompoundTag());
        boolean currentlyHasMaster = currentNbt.getBoolean("hasMaster");

        if (neighborBlock instanceof ChildBlock) {
            BlockEntity neighborBlockEntity = world.getBlockEntity(neighborPos);
            CompoundTag neighborNbt = neighborBlockEntity.toTag(new CompoundTag());
            boolean neighborHasMaster = neighborNbt.getBoolean("hasMaster");
            if (currentlyHasMaster && !neighborHasMaster) {
                CompoundTag masterPos = currentNbt.getCompound("masterPos");
                CompoundTag neighborMasterPos = neighborNbt.getCompound("masterPos");
                if (masterPos.equals(neighborMasterPos)) {
                    currentNbt.putBoolean("hasMaster", false);
                    blockEntity.fromTag(currentNbt);
                    blockEntity.markDirty();
                    world.updateNeighbors(pos, block);
                } else {
                    neighborBlockEntity.fromTag(currentNbt);
                    neighborBlockEntity.markDirty();
                    world.updateNeighbors(neighborPos, neighborBlock);
                }
            }
            if (!currentlyHasMaster && neighborHasMaster) {
                blockEntity.fromTag(neighborNbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, block);
            }
        } else if (neighborBlock instanceof MasterBlock) {
            if (!currentlyHasMaster) {
                CompoundTag masterPos = new CompoundTag();
                masterPos.putInt("x", neighborPos.getX());
                masterPos.putInt("y", neighborPos.getY());
                masterPos.putInt("z", neighborPos.getZ());
                currentNbt.put("masterPos", masterPos);
                blockEntity.fromTag(currentNbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, block);
            }
        } else if (currentlyHasMaster) {
            CompoundTag master = currentNbt.getCompound("masterPos");
            BlockPos masterPos = new BlockPos(master.getInt("x"), master.getInt("y"), master.getInt("z"));
            if (!(world.getBlockState(masterPos).getBlock() instanceof MasterBlock)) {
                currentNbt.putBoolean("hasMaster", false);
                blockEntity.fromTag(currentNbt);
                blockEntity.markDirty();
                world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
            }
        }
    }

}
