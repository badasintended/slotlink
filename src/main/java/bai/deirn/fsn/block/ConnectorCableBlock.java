package bai.deirn.fsn.block;

import bai.deirn.fsn.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class ConnectorCableBlock extends CableBlock {

    public ConnectorCableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean canConnect(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        boolean result = block instanceof ModBlock;
        if (!result && block.hasBlockEntity()) {
            result = Inventory.class.isAssignableFrom(world.getBlockEntity(pos).getClass());
        }
        return result;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        VoxelShape end = Utils.cuboid(5, 5, 5, 6, 6, 6);
        VoxelShape result = super.getOutlineShape(state, view, pos, context);
        return VoxelShapes.union(result, end);
    }

}
