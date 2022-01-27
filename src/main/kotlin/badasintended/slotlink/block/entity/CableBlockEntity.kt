package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.NodeType
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class CableBlockEntity(pos: BlockPos, state: BlockState) :
    ChildBlockEntity(BlockEntityTypes.CABLE, NodeType.CABLE, pos, state)
