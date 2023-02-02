package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.storage.FilterFlags
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
class ExportCableBlockEntity(pos: BlockPos, state: BlockState) :
    TransferCableBlockEntity(Blocks.EXPORT_CABLE, BlockEntityTypes.EXPORT_CABLE, NodeType.EXPORT, pos, state) {

    override var side = Direction.UP

    override fun getSource(world: World, master: MasterBlockEntity): Storage<ItemVariant> {
        return master.getStorages(world, FilterFlags.EXTRACT)
    }

    override fun getTarget(world: World, master: MasterBlockEntity): Storage<ItemVariant> {
        return getStorage(world, side, FilterFlags.INSERT)
    }

}
