package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.network.NodeType
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.storage.FilterFlags
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos

class RequestBlockEntity(pos: BlockPos, state: BlockState) :
    ChildBlockEntity(BlockEntityTypes.REQUEST, NodeType.REQUEST, pos, state),
    NamedScreenHandlerFactory {

    val watchers = ObjectOpenHashSet<BlockEntityWatcher<RequestBlockEntity>>()

    override fun markRemoved() {
        super.markRemoved()
        watchers.forEach { it.onRemoved() }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        val world = getWorld() ?: return null
        network?.also { network ->
            val master = network.master ?: return null
            val storages = master.getStorages(world, FilterFlags.INSERT, true)
            val handler = RequestScreenHandler(syncId, inv, storages, this, master)
            watchers.add(handler)
            master.watchers.add(handler)
            master.markForcedChunks()
            return handler
        }
        return null
    }

    override fun getDisplayName() = Text.translatable("container.slotlink.request")!!

}
