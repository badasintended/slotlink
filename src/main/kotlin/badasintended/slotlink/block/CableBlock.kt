package badasintended.slotlink.block

import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.util.bbCuboid
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Material
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

open class CableBlock(id: String = "cable", be: () -> BlockEntity = ::CableBlockEntity) : ChildBlock(id, be, SETTINGS) {

    companion object {

        val SETTINGS: Settings = FabricBlockSettings
            .of(Material.GLASS)
            .breakByHand(true)
            .breakByTool(FabricToolTags.PICKAXES)
            .hardness(3f)

        val NORTH: BooleanProperty = BooleanProperty.of("north")
        val SOUTH: BooleanProperty = BooleanProperty.of("south")
        val EAST: BooleanProperty = BooleanProperty.of("east")
        val WEST: BooleanProperty = BooleanProperty.of("west")
        val UP: BooleanProperty = BooleanProperty.of("up")
        val DOWN: BooleanProperty = BooleanProperty.of("down")

        val properties = mapOf(
            Direction.NORTH to NORTH,
            Direction.SOUTH to SOUTH,
            Direction.EAST to EAST,
            Direction.WEST to WEST,
            Direction.UP to UP,
            Direction.DOWN to DOWN
        )

        val center = bbCuboid(6, 6, 6, 4, 4, 4)

        val shapes = mapOf(
            NORTH to bbCuboid(6, 6, 0, 4, 4, 10),
            SOUTH to bbCuboid(6, 6, 6, 4, 4, 10),
            EAST to bbCuboid(6, 6, 6, 10, 4, 4),
            WEST to bbCuboid(0, 6, 6, 10, 4, 4),
            UP to bbCuboid(6, 6, 6, 4, 10, 4),
            DOWN to bbCuboid(6, 0, 6, 4, 10, 4)
        )

    }

    private val voxelCache = hashMapOf<String, VoxelShape>()

    protected open fun canConnect(world: WorldAccess, neighborPos: BlockPos): Boolean {
        val block = world.getBlockState(neighborPos).block
        return block is ModBlock
    }

    protected open fun center() = center

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
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return state.with(properties[facing], canConnect(world, neighborPos))
    }

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
        var str = ""
        shapes.keys.forEach { str += if (state[it]) "1" else "0" }
        return voxelCache.computeIfAbsent(str) {
            VoxelShapes.union(center(), *shapes.filter { state[it.key] }.values.toTypedArray())
        }
    }

}
