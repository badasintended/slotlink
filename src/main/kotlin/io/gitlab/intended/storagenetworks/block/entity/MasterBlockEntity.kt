package io.gitlab.intended.storagenetworks.block.entity

import io.gitlab.intended.storagenetworks.block.LinkCableBlock
import io.gitlab.intended.storagenetworks.block.entity.type.BlockEntityTypeRegistry
import io.gitlab.intended.storagenetworks.tag2Pos
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.World

class MasterBlockEntity : BlockEntity(BlockEntityTypeRegistry.MASTER) {

    private var linkCables = ListTag()

    private fun validateConnectors(world: World) {
        val linkCableSet = HashSet<CompoundTag>()
        linkCables.forEach { linkCableTag ->
            linkCableTag as CompoundTag
            val linkCablePos = tag2Pos(linkCableTag)
            val linkCableBlock = world.getBlockState(linkCablePos).block
            if (linkCableBlock is LinkCableBlock) {
                val linkCableNbt = world.getBlockEntity(linkCablePos)!!.toTag(CompoundTag())

                val linkCableHasMaster = linkCableNbt.getBoolean("hasMaster")
                val linkCableMasterPos = tag2Pos(linkCableNbt.getCompound("masterPos"))

                if (linkCableHasMaster and (linkCableMasterPos == pos)) {
                    linkCableSet.add(linkCableTag)
                }
            }
        }
        linkCables.clear()
        linkCables.addAll(linkCableSet)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.put("linkCables", linkCables)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        linkCables = tag.getList("linkCables", NbtType.COMPOUND)
    }

    override fun markDirty() {
        if (world != null) validateConnectors(world!!)
        super.markDirty()
    }

}
