package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos

class CableBlockEntity(pos: BlockPos, state: BlockState) : ChildBlockEntity(BlockEntityTypes.CABLE, pos, state)
