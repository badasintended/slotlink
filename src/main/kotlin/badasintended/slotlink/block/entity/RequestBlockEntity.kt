package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.BlockEntityWatcher
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.TranslatableText

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypes.REQUEST), NamedScreenHandlerFactory {

    val watchers = ObjectOpenHashSet<BlockEntityWatcher<RequestBlockEntity>>()

    override fun markRemoved() {
        super.markRemoved()
        watchers.forEach { it.onRemoved() }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler? {
        val world = getWorld() ?: return null
        if (!hasMaster) return null
        val master = world.getBlockEntity(masterPos) ?: return null
        if (master !is MasterBlockEntity) return null
        val handler = RequestScreenHandler(syncId, inv, master.getInventories(world, true), this, master)
        watchers.add(handler)
        master.watchers.add(handler)
        master.markForcedChunks()
        return handler
    }

    override fun getDisplayName() = TranslatableText("container.slotlink.request")

}
