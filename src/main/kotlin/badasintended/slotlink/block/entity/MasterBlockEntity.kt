package badasintended.slotlink.block.entity

import badasintended.slotlink.init.BlockEntityTypes
import badasintended.slotlink.inventory.FilteredInventory
import badasintended.slotlink.util.BlockPosSet
import badasintended.slotlink.util.MasterWatcher
import badasintended.slotlink.util.fromTag
import badasintended.slotlink.util.toTag
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import java.util.SortedSet
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import net.minecraft.world.World

class MasterBlockEntity : ModBlockEntity(BlockEntityTypes.MASTER), Tickable {

    val linkPos = BlockPosSet()
    val importPos = BlockPosSet()
    val exportPos = BlockPosSet()
    var watchers = hashSetOf<MasterWatcher>()

    private val linkCables = ObjectArrayList<LinkCableBlockEntity>()
    private val importCables = ObjectArrayList<ImportCableBlockEntity>()
    private val exportCables = ObjectArrayList<ExportCableBlockEntity>()

    private val invSet = ObjectLinkedOpenHashSet<FilteredInventory>()

    private var tick = 0
    val forcedChunks = hashSetOf<Pair<Int, Int>>()

    fun getInventories(world: World, request: Boolean = false): SortedSet<FilteredInventory> {
        invSet.clear()
        linkCables.clear()
        linkPos.forEach { tag ->
            val blockEntity = world.getBlockEntity(tag)
            if (blockEntity is LinkCableBlockEntity) linkCables.add(blockEntity)
        }

        linkCables.sortByDescending { it.priority }
        linkCables.forEach { invSet.add(it.getInventory(world, this, request)) }

        return invSet
    }

    fun unmarkForcedChunks() = world?.let { world ->
        if (!world.isClient && watchers.isEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, false)
            }
            forcedChunks.clear()
        }
    }

    fun markForcedChunks() = world?.let { world ->
        if (!world.isClient && watchers.isNotEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, true)
            }
        }
    }

    private fun validateCables(world: World) {
        linkPos.removeIf r@{
            val be = world.getBlockEntity(it)
            if (be is LinkCableBlockEntity) {
                if (be.hasMaster && be.masterPos == pos) {
                    return@r false
                }
            }
            return@r true
        }

        importPos.removeIf r@{
            val be = world.getBlockEntity(it)
            if (be is ImportCableBlockEntity) {
                if (be.hasMaster && be.masterPos == pos) {
                    return@r false
                }
            }
            return@r true
        }

        exportPos.removeIf r@{
            val be = world.getBlockEntity(it)
            if (be is ExportCableBlockEntity) {
                if (be.hasMaster && be.masterPos == pos) {
                    return@r false
                }
            }
            return@r true
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkCables", linkPos.toTag())
        tag.put("exportCables", exportPos.toTag())
        tag.put("importCables", importPos.toTag())

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        linkPos.fromTag(tag.getList("linkCables", NbtType.COMPOUND))
        exportPos.fromTag(tag.getList("exportCables", NbtType.COMPOUND))
        importPos.fromTag(tag.getList("importCables", NbtType.COMPOUND))
    }

    override fun markDirty() {
        super.markDirty()

        val world = getWorld() ?: return
        validateCables(world)
    }

    override fun markRemoved() {
        super.markRemoved()
        watchers.forEach { it.onMasterRemoved() }
    }

    override fun tick() {
        tick++
        if (tick == 10) {
            importCables.clear()
            val world = getWorld() ?: return
            importPos.forEach {
                val blockEntity = world.getBlockEntity(it)
                if (blockEntity is ImportCableBlockEntity) importCables.add(blockEntity)
            }
            importCables.sortByDescending { it.priority }
            for (cable in importCables) {
                if (cable.transfer(world, this)) break
            }
        } else if (tick == 20) {
            tick = 0
            exportCables.clear()
            val world = getWorld() ?: return
            exportPos.forEach {
                val blockEntity = world.getBlockEntity(it)
                if (blockEntity is ExportCableBlockEntity) exportCables.add(blockEntity)
            }
            exportCables.sortByDescending { it.priority }
            for (cable in exportCables) {
                if (cable.transfer(world, this)) break
            }
        }
    }

}
