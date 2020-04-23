package bai.deirn.fsn.block;

import bai.deirn.fsn.Utils;
import bai.deirn.fsn.block.entity.CableBlockEntity;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.Map;

public class CableBlock extends ChildBlock {

    public static final Settings SETTINGS = FabricBlockSettings
            .of(Material.GLASS)
            .breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .hardness(3F)
            .build();

    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty WEST = BooleanProperty.of("west");
    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");

    public CableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new CableBlockEntity();
    }

    private boolean canConnect(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return canConnect(world, pos, state);
    }

    protected boolean canConnect(World world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        return block instanceof FSNBlock;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return this.getDefaultState()
                .with(NORTH, this.canConnect(world, pos.north()))
                .with(SOUTH, this.canConnect(world, pos.south()))
                .with(EAST, this.canConnect(world, pos.east()))
                .with(WEST, this.canConnect(world, pos.west()))
                .with(UP, this.canConnect(world, pos.up()))
                .with(DOWN, this.canConnect(world, pos.down()));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
        super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
        Map<Direction, BooleanProperty> propertyMap = ImmutableMap.<Direction, BooleanProperty>builder()
                .put(Direction.NORTH, NORTH)
                .put(Direction.SOUTH, SOUTH)
                .put(Direction.EAST, EAST)
                .put(Direction.WEST, WEST)
                .put(Direction.UP, UP)
                .put(Direction.DOWN, DOWN)
                .build();

        return state.with(propertyMap.get(facing), this.canConnect(world.getWorld(), neighborPos, neighborState));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        VoxelShape north = Utils.cuboid(6, 6, 0, 4, 4, 10);
        VoxelShape south = Utils.cuboid(6, 6, 6, 4, 4, 10);
        VoxelShape east = Utils.cuboid(6, 6, 6, 10, 4, 4);
        VoxelShape west = Utils.cuboid(0, 6, 6, 10, 4, 4);
        VoxelShape up = Utils.cuboid(6, 6, 6, 4, 10, 4);
        VoxelShape down = Utils.cuboid(6, 0, 6, 4, 10, 4);

        boolean n = state.get(NORTH);
        boolean s = state.get(SOUTH);
        boolean e = state.get(EAST);
        boolean w = state.get(WEST);
        boolean u = state.get(UP);
        boolean d = state.get(DOWN);

        VoxelShape result = Utils.cuboid(6, 6, 6, 4, 4, 4);

        if (n) result = VoxelShapes.union(result, north);
        if (s) result = VoxelShapes.union(result, south);
        if (e) result = VoxelShapes.union(result, east);
        if (w) result = VoxelShapes.union(result, west);
        if (u) result = VoxelShapes.union(result, up);
        if (d) result = VoxelShapes.union(result, down);

        return result;
    }

}
