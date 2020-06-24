package badasintended.slotlink.common

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.math.BlockPos

/**
 * Apparently, [NbtHelper.fromBlockPos] and [NbtHelper.toBlockPos]
 * use uppercase XYZ instead of lowercase xyz and that drive me nuts so I made this functions instead.
 *
 * FIXME: fix this kinda ocd.
 * @see tag2Pos
 */
fun pos2Tag(pos: BlockPos): CompoundTag {
    val tag = CompoundTag()
    tag.putInt("x", pos.x)
    tag.putInt("y", pos.y)
    tag.putInt("z", pos.z)
    return tag
}

/**
 * @see pos2Tag
 */
fun tag2Pos(tag: CompoundTag): BlockPos {
    return BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"))
}
