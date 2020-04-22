package bai.deirn.fsn.block;

import bai.deirn.fsn.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ConnectorCableBlock extends CableBlock {

    public ConnectorCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canConnect(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block.hasBlockEntity()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity != null && Inventory.class.isAssignableFrom(blockEntity.getClass());
        } else {
            return block instanceof FSNBlock;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        VoxelShape end = Utils.cuboid(5, 5, 5, 6, 6, 6);
        VoxelShape result = super.getOutlineShape(state, view, pos, context);
        return VoxelShapes.union(result, end);
    }

}
