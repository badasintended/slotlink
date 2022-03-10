package badasintended.slotlink.block

import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.util.BlockEntityBuilder
import badasintended.slotlink.util.bbCuboid
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties.DOWN
import net.minecraft.state.property.Properties.EAST
import net.minecraft.state.property.Properties.NORTH
import net.minecraft.state.property.Properties.SOUTH
import net.minecraft.state.property.Properties.UP
import net.minecraft.state.property.Properties.WEST
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

open class CableBlock(id: String = "cable", be: BlockEntityBuilder = ::CableBlockEntity) :
    ChildBlock(id, be, SETTINGS) {

    companion object {

        val SETTINGS: Settings = FabricBlockSettings
            .of(Material.GLASS)
            .hardness(3f)

        val PROPERTIES = mapOf(
            Direction.NORTH to NORTH,
            Direction.SOUTH to SOUTH,
            Direction.EAST to EAST,
            Direction.WEST to WEST,
            Direction.UP to UP,
            Direction.DOWN to DOWN
        )

        val centerShape = bbCuboid(6, 6, 6, 4, 4, 4)

        val sideShapes = mapOf(
            NORTH to bbCuboid(6, 6, 0, 4, 4, 10),
            SOUTH to bbCuboid(6, 6, 6, 4, 4, 10),
            EAST to bbCuboid(6, 6, 6, 10, 4, 4),
            WEST to bbCuboid(0, 6, 6, 10, 4, 4),
            UP to bbCuboid(6, 6, 6, 4, 10, 4),
            DOWN to bbCuboid(6, 0, 6, 4, 10, 4)
        )

        val voxelCache = Int2ObjectOpenHashMap<VoxelShape>()

    }

    init {
        for (property in PROPERTIES.values) {
            defaultState = defaultState.with(property, false)
        }
    }

    protected open fun connect(
        state: BlockState,
        direction: Direction,
        world: WorldAccess,
        neighborState: BlockState,
        neighborPos: BlockPos
    ): BlockState {
        val block = neighborState.block
        return state.with(PROPERTIES[direction], block is ModBlock)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos

        var state = defaultState
        for (direction in DIRECTIONS) {
            val offset = pos.offset(direction)
            state = connect(state, direction, world, world.getBlockState(offset), offset)
        }
        return state
    }

    @Suppress("DEPRECATION")
    override fun getStateForNeighborUpdate(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return connect(
            super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos),
            direction,
            world,
            neighborState,
            neighborPos
        )
    }

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
        var key = 0
        sideShapes.keys.forEach { key = (key shl 1) + if (state[it]) 1 else 0 }
        return voxelCache.getOrPut(key) {
            VoxelShapes.union(centerShape, *sideShapes.filter { state[it.key] }.values.toTypedArray())
        }
    }

}
