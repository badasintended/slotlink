package badasintended.slotlink.block.entity

import badasintended.slotlink.common.registry.BlockEntityTypeRegistry
import badasintended.slotlink.common.util.MasterWatcher
import badasintended.slotlink.common.util.toPos
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.inventory.Inventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MasterBlockEntity : BlockEntity(BlockEntityTypeRegistry.MASTER), Tickable {

    var linkCables = ListTag()
    var transferCables = ListTag()

    var watchers = hashSetOf<MasterWatcher>()

    private var tick = 0

    fun getLinkedInventories(world: World): Map<BlockPos, Inventory> {
        val linkedMap = linkedMapOf<BlockPos, Inventory>()

        linkCables.forEach { linkCablePosTag ->
            linkCablePosTag as CompoundTag
            val cablePos = linkCablePosTag.toPos()
            val cableBlockEntity = world.getBlockEntity(cablePos)

            if (cableBlockEntity is LinkCableBlockEntity) {
                val inventory = cableBlockEntity.getLinkedInventory(world)
                if (inventory != null) linkedMap[cableBlockEntity.linkedPos.toPos()] = inventory
            }
        }

        return linkedMap
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

        val transferCableSet = linkedSetOf<CompoundTag>()
        transferCables.forEach { tag ->
            val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
            if (blockEntity is TransferCableBlockEntity) {
                if (blockEntity.hasMaster and (blockEntity.masterPos.toPos() == pos)) {
                    transferCableSet.add(tag)
                }
            }
        }
        transferCables.clear()
        transferCables.addAll(transferCableSet)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkCables", linkCables)
        tag.put("transferCables", transferCables)

        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)

        linkCables = tag.getList("linkCables", NbtType.COMPOUND)
        transferCables = tag.getList("transferCables", NbtType.COMPOUND)
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
        if (tick == 20) {
            tick = 0
            val world = getWorld() ?: return
            transferCables.forEach { tag ->
                val blockEntity = world.getBlockEntity((tag as CompoundTag).toPos())
                if (blockEntity is TransferCableBlockEntity) blockEntity.transfer(world, this)
            }
        }
    }

}
