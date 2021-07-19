package badasintended.slotlink.util

import badasintended.slotlink.Slotlink
import io.netty.buffer.Unpooled
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.tag.Tag
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import org.apache.logging.log4j.LogManager

typealias BlockEntityBuilder = (BlockPos, BlockState) -> BlockEntity

fun BlockPos.toNbt(): NbtCompound {
    val tag = NbtCompound()
    tag.putInt("x", x)
    tag.putInt("y", y)
    tag.putInt("z", z)
    return tag
}

fun BlockPos.toArray(): IntArray {
    return intArrayOf(x, y, z)
}

fun IntArray.toPos(): BlockPos {
    return BlockPos(get(0), get(1), get(2))
}

fun NbtCompound.toPos(): BlockPos {
    return BlockPos(getInt("x"), getInt("y"), getInt("z"))
}

fun PlayerEntity.actionBar(key: String, vararg args: Any) {
    if (this is ServerPlayerEntity) sendMessage(
        TranslatableText(key, *args), true
    )
}

fun PlayerEntity.chat(key: String, vararg args: Any) {
    if (this is ServerPlayerEntity) sendMessage(
        TranslatableText(key, *args), false
    )
}

fun buf(): PacketByteBuf {
    return PacketByteBuf(Unpooled.buffer())
}

/**
 * Generates [VoxelShape] based on the position that shows on [Blockbench](https://blockbench.net).
 * No thinking required!
 */
fun bbCuboid(xPos: Int, yPos: Int, zPos: Int, xSize: Int, ySize: Int, zSize: Int): VoxelShape {
    val xMin = xPos / 16.0
    val yMin = yPos / 16.0
    val zMin = zPos / 16.0
    val xMax = (xPos + xSize) / 16.0
    val yMax = (yPos + ySize) / 16.0
    val zMax = (zPos + zSize) / 16.0
    return VoxelShapes.cuboid(xMin, yMin, zMin, xMax, yMax, zMax)
}

fun Direction.next(): Direction {
    return Direction.byId(id + 1)
}

fun PacketByteBuf.writeFilter(filter: List<Pair<ItemStack, Boolean>>) {
    filter.forEach {
        writeItemStack(it.first)
        writeBoolean(it.second)
    }
}

fun PacketByteBuf.readFilter(size: Int = 9): MutableList<Pair<ItemStack, Boolean>> {
    val list = arrayListOf<Pair<ItemStack, Boolean>>()
    for (i in 0 until size) {
        list.add(readItemStack() to readBoolean())
    }
    return list
}

fun modId(path: String) = Identifier(Slotlink.ID, path)

@Suppress("unused")
val log = LogManager.getLogger(Slotlink.ID)!!

inline fun s2c(player: PlayerEntity, id: Identifier, buf: PacketByteBuf.() -> Unit) {
    player as ServerPlayerEntity
    ServerPlayNetworking.send(player, id, buf().apply(buf))
}

fun s2c(player: PlayerEntity, packet: Packet<*>) {
    player as ServerPlayerEntity
    ServerPlayNetworking.getSender(player).sendPacket(packet)
}

val ignoredTag: Tag<Block> = TagRegistry.block(modId("ignored"))

fun ItemStack.isItemAndTagEqual(other: ItemStack): Boolean {
    return ItemStack.areNbtEqual(this, other) && ItemStack.areItemsEqual(this, other)
}

fun ItemStack.merge(from: ItemStack): Pair<ItemStack, ItemStack> {
    val f = from.copy()
    val t = this.copy()

    if (isEmpty) return f to ItemStack.EMPTY
    if (!isItemAndTagEqual(f) || count >= maxCount || f.isEmpty) return t to f

    val max = (maxCount - count).coerceAtLeast(0)
    val added = min(max, f.count)

    t.increment(added)
    f.decrement(added)

    return t to f
}

fun Pair<ItemStack, ItemStack>.allEmpty() = first.isEmpty && second.isEmpty

var Pair<Inventory, Int>.stack: ItemStack
    get() = first.getStack(second)
    set(value) = first.setStack(second, value)


fun Int.toFormattedString(): String = when {
    this < 1000 -> "$this"
    else -> {
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        String.format("%.1f%c", this / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
    }
}
