package io.gitlab.intended.storagenetworks.block

import net.minecraft.block.BlockState
import net.minecraft.entity.EntityContext
import net.minecraft.inventory.Inventory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

abstract class ConnectorCableBlock(id: String) : CableBlock(id) {

    override fun canConnect(world: World, pos: BlockPos, state: BlockState): Boolean {
        val block = state.block
        var result = block is ModBlock

        if (!result and block.hasBlockEntity()) {
            result = Inventory::class.java.isAssignableFrom(world.getBlockEntity(pos)!!.javaClass)
        }

        return result
    }

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, context: EntityContext): VoxelShape {
        val end = cuboid(5, 5, 5, 6, 6, 6)
        val result = super.getOutlineShape(state, view, pos, context)
        return VoxelShapes.union(result, end)
    }

}