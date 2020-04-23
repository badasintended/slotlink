package bai.deirn.fsn.block;

import bai.deirn.fsn.block.entity.MasterBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;

import java.util.List;

public class MasterBlock extends FSNBlock implements BlockEntityProvider {

    public MasterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new MasterBlockEntity();
    }

    @Override
    public void buildTooltip(ItemStack stack, BlockView view, List<Text> tooltip, TooltipContext options) {
        super.buildTooltip(stack, view, tooltip, options);
        tooltip.add(new TranslatableText(stack.getTranslationKey()+".tooltip2"));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        Block neighborBlock = neighborState.getBlock();

        if (neighborBlock instanceof ChildBlock) {
            BlockEntity neighborBlockEntity = world.getBlockEntity(neighborPos);
            CompoundTag neighborNbt = neighborBlockEntity.toTag(new CompoundTag());
            boolean neighborHasMaster = neighborNbt.getBoolean("hasMaster");

            if (!neighborHasMaster) {
                CompoundTag masterPos = new CompoundTag();
                masterPos.putInt("x", pos.getX());
                masterPos.putInt("y", pos.getY());
                masterPos.putInt("z", pos.getZ());
                neighborNbt.put("masterPos", masterPos);
                neighborNbt.putBoolean("hasMaster", true);
                neighborBlockEntity.fromTag(neighborNbt);
                neighborBlockEntity.markDirty();
            }
        }

        return state;
    }

}
