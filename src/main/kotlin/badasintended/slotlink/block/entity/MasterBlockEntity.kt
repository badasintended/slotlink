package badasintended.slotlink.block.entity

import badasintended.slotlink.mixin.DoubleInventoryAccessor
import badasintended.slotlink.registry.BlockEntityTypeRegistry
import badasintended.slotlink.util.MasterWatcher
import badasintended.slotlink.util.toPos
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.DoubleInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Tickable
import net.minecraft.world.World

class MasterBlockEntity : BlockEntity(BlockEntityTypeRegistry.MASTER), Tickable {

    var linkCables = ListTag()

    var importCables = ListTag()

    var exportCables = ListTag()

    var watchers = hashSetOf<MasterWatcher>()

    private var tick = 0

    val forcedChunks = hashSetOf<Pair<Int, Int>>()

    fun getLinkedInventories(world: World, request: Boolean = false): Map<Inventory, Pair<Boolean, Set<Item>>> {
        val linkedMap = linkedMapOf<Inventory, Pair<Boolean, Set<Item>>>()

        val cables = arrayListOf<LinkCableBlockEntity>()

        linkCables.sortedByDescending { (it as CompoundTag).getInt("p") }.forEach { linkCablePosTag ->
            linkCablePosTag as CompoundTag
            val cablePos = linkCablePosTag.toPos()
            val cable = world.getBlockEntity(cablePos)

            if (cable is LinkCableBlockEntity) cables.add(cable)
        }

        cables.sortByDescending { it.priority }
        for (cable in cables) {
            val inventory = cable.getLinkedInventory(world, this, request, request) ?: continue
            val key = inventory.first ?: continue
            if (key is DoubleInventory) {
                key as DoubleInventoryAccessor
                if (linkedMap.keys
                        .filterIsInstance<DoubleInventory>()
                        .any { it.isPart(key.first) or it.isPart(key.second) }
                ) continue
            }
            linkedMap[key] = inventory.second
        }

        return linkedMap
    }

    fun unloadForcedChunks(world: World) {
        if (!world.isClient and watchers.isEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, false)
            }
        }
        forcedChunks.clear()
    }

    fun forceChunk(world: World) {
        if (!world.isClient and watchers.isNotEmpty()) {
            world as ServerWorld
            forcedChunks.forEach {
                world.setChunkForced(it.first, it.second, true)
            }
        }
    }

    private fun validateCables(world: World) {
        val linkCableSet = linkedSetOf<CompoundTag>()
        linkCables.forEach { tag ->
            val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
            if (blockEntity is LinkCableBlockEntity) {
                if (blockEntity.hasMaster and (blockEntity.masterPos.toPos() == pos)) {
                    linkCableSet.add(tag)
                }
            }
        }
        linkCables.clear()
        linkCables.addAll(linkCableSet)

        val importCableSet = linkedSetOf<CompoundTag>()
        importCables.forEach { tag ->
            val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
            if (blockEntity is ImportCableBlockEntity) {
                if (blockEntity.hasMaster and (blockEntity.masterPos.toPos() == pos)) {
                    importCableSet.add(tag)
                }
            }
        }
        importCables.clear()
        importCables.addAll(importCableSet)

        val exportCableSet = linkedSetOf<CompoundTag>()
        exportCables.forEach { tag ->
            val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
            if (blockEntity is ExportCableBlockEntity) {
                if (blockEntity.hasMaster and (blockEntity.masterPos.toPos() == pos)) {
                    exportCableSet.add(tag)
                }
            }
        }
        exportCables.clear()
        exportCables.addAll(exportCableSet)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkCables", linkCables)
        tag.put("exportCables", exportCables)
        tag.put("importCables", importCables)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        linkCables = tag.getList("linkCables", NbtType.COMPOUND)
        exportCables = tag.getList("exportCables", NbtType.COMPOUND)
        importCables = tag.getList("importCables", NbtType.COMPOUND)
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
            val world = getWorld() ?: return
            val cables = arrayListOf<ImportCableBlockEntity>()
            importCables.forEach { tag ->
                val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
                if (blockEntity is ImportCableBlockEntity) cables.add(blockEntity)
            }
            cables.sortByDescending { it.priority }
            for (cable in cables) {
                if (cable.transfer(world, this)) break
            }
        } else if (tick == 20) {
            tick = 0
            val world = getWorld() ?: return
            val cables = arrayListOf<ExportCableBlockEntity>()
            exportCables.forEach { tag ->
                val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
                if (blockEntity is ExportCableBlockEntity) cables.add(blockEntity)
            }
            cables.sortByDescending { it.priority }
            for (cable in cables) {
                if (cable.transfer(world, this)) break
            }
        }
    }

}
