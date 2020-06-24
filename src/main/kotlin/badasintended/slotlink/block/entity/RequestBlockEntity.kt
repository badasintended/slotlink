package badasintended.slotlink.block.entity

import net.minecraft.container.Container
import net.minecraft.container.ContainerFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.CompoundTag

class RequestBlockEntity : ChildBlockEntity(BlockEntityTypeRegistry.REQUEST) {

    private var lastSort = 0

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)

        tag.putInt("lastSort", lastSort)

        return tag
    }

    override fun fromTag(tag: CompoundTag) {
        super.fromTag(tag)

        lastSort = tag.getInt("lastSort")
    }

}
