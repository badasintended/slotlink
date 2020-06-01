package io.gitlab.intended.storagenetworks.block

import com.google.common.collect.ImmutableMap
import io.gitlab.intended.storagenetworks.block.entity.CableBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.EntityContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.IWorld
import net.minecraft.world.World

open class CableBlock(id: String) : ChildBlock(id, SETTINGS) {

    companion object {
        val SETTINGS: Settings =
            FabricBlockSettings.of(Material.GLASS).breakByHand(true).breakByTool(FabricToolTags.PICKAXES).hardness(3f)

        val NORTH = BooleanProperty.of("north")
        val SOUTH = BooleanProperty.of("south")
        val EAST = BooleanProperty.of("east")
        val WEST = BooleanProperty.of("west")
        val UP = BooleanProperty.of("up")
        val DOWN = BooleanProperty.of("down")
    }

    override fun createBlockEntity(view: BlockView): BlockEntity = CableBlockEntity()

    protected open fun canConnect(world: World, pos: BlockPos, state: BlockState): Boolean {
        return state.block is io.gitlab.intended.storagenetworks.block.ModBlock
    }

    private fun canConnect(world: World, pos: BlockPos): Boolean {
        return canConnect(world, pos, world.getBlockState(pos))
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos

        return defaultState
            .with(NORTH, canConnect(world, pos.north()))
            .with(SOUTH, canConnect(world, pos.south()))
            .with(EAST, canConnect(world, pos.east()))
            .with(WEST, canConnect(world, pos.west()))
            .with(UP, canConnect(world, pos.up()))
            .with(DOWN, canConnect(world, pos.down()))
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        facing: Direction,
        neighborState: BlockState,
        world: IWorld,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos)

        val propertyMap = ImmutableMap.builder<Direction, BooleanProperty>()
            .put(Direction.NORTH, NORTH)
            .put(Direction.SOUTH, SOUTH)
            .put(Direction.EAST, EAST)
            .put(Direction.WEST, WEST)
            .put(Direction.UP, UP)
            .put(Direction.DOWN, DOWN)
            .build()

        return state.with(propertyMap[facing], canConnect(world.world, neighborPos, neighborState))
    }

    override fun getOutlineShape(
        state: BlockState,
        view: BlockView,
        pos: BlockPos,
        context: EntityContext
    ): VoxelShape {
        val north = cuboid(6, 6, 0, 4, 4, 10)
        val south = cuboid(6, 6, 6, 4, 4, 10)
        val east = cuboid(6, 6, 6, 10, 4, 4)
        val west = cuboid(0, 6, 6, 10, 4, 4)
        val up = cuboid(6, 6, 6, 4, 10, 4)
        val down = cuboid(6, 0, 6, 4, 10, 4)

        var result = cuboid(6, 6, 6, 4, 4, 4)

        if (state[NORTH]) result = VoxelShapes.union(result, north)
        if (state[SOUTH]) result = VoxelShapes.union(result, south)
        if (state[EAST]) result = VoxelShapes.union(result, east)
        if (state[WEST]) result = VoxelShapes.union(result, west)
        if (state[UP]) result = VoxelShapes.union(result, up)
        if (state[DOWN]) result = VoxelShapes.union(result, down)

        return result
    }

}