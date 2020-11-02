package badasintended.slotlink.util

import badasintended.slotlink.Slotlink
import com.google.common.collect.ImmutableMap
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
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
import org.apache.logging.log4j.Logger
import kotlin.math.min

fun BlockPos.toTag(): CompoundTag {
    val tag = CompoundTag()
    tag.putInt("x", x)
    tag.putInt("y", y)
    tag.putInt("z", z)
    return tag
}

fun CompoundTag.toPos(): BlockPos {
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

fun BlockPos.around(): ImmutableMap<Direction, BlockPos> {
    return ImmutableMap
        .builder<Direction, BlockPos>()
        .put(Direction.NORTH, north())
        .put(Direction.SOUTH, south())
        .put(Direction.EAST, east())
        .put(Direction.WEST, west())
        .put(Direction.UP, up())
        .put(Direction.DOWN, down())
        .build()
}

@Environment(EnvType.CLIENT)
fun Direction.texture(): Identifier {
    return modId("textures/gui/side_${asString()}.png")
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

val log: Logger = LogManager.getLogger(Slotlink.ID)

@Environment(EnvType.CLIENT)
fun getClient(): MinecraftClient = MinecraftClient.getInstance()

@Environment(EnvType.CLIENT)
fun c2s(id: Identifier, buf: PacketByteBuf) {
    ClientSidePacketRegistry.INSTANCE.sendToServer(id, buf)
}

fun s2c(player: PlayerEntity, id: Identifier, buf: PacketByteBuf) {
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, id, buf)
}

fun s2c(player: PlayerEntity, packet: Packet<*>) {
    ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet)
}

val ignoredTag: Tag<Block> = TagRegistry.block(modId("ignored"))

fun ItemStack.isItemAndTagEqual(other: ItemStack): Boolean {
    return ItemStack.areTagsEqual(this, other) and ItemStack.areItemsEqual(this, other)
}

fun ItemStack.merge(from: ItemStack): Pair<ItemStack, ItemStack> {
    val f = from.copy()
    val t = this.copy()

    if (isEmpty) return f to ItemStack.EMPTY
    if (!isItemAndTagEqual(f) or (count >= maxCount) or f.isEmpty) return t to f

    val max = (maxCount - count).coerceAtLeast(0)
    val added = min(max, f.count)

    t.increment(added)
    f.decrement(added)

    return t to f
}

val guiTexture = modId("textures/gui/gui.png")

@Environment(EnvType.CLIENT)
fun bindGuiTexture() {
    getClient().textureManager.bindTexture(guiTexture)
}

private typealias DH = DrawableHelper

fun drawNinePatch(matrices: MatrixStack, x: Int, y: Int, w: Int, h: Int, u: Float, v: Float, ltrb: Int, cm: Int) {
    drawNinePatch(matrices, x, y, w, h, u, v, ltrb, cm, ltrb)
}

fun drawNinePatch(matrices: MatrixStack, x: Int, y: Int, w: Int, h: Int, u: Float, v: Float, lt: Int, cm: Int, rb: Int) {
    drawNinePatch(matrices, x, y, w, h, u, v, lt, cm, rb, lt, cm, rb)
}

/**
 * Wed Oct 14 01:43:12 PM UTC 2020
 * well i managed to write this shit
 *
 * @param x square x position
 * @param y square y position
 * @param w square width
 * @param h square height
 * @param u texture left position (in pixel)
 * @param v texture top position (in pixel)
 * @param l nine-patch left size
 * @param c nine-patch center size
 * @param r nine-patch right size
 * @param t nine-patch top size
 * @param m nine-patch middle size
 * @param b nine-patch bottom size
 */
@Environment(EnvType.CLIENT)
fun drawNinePatch(
    matrices: MatrixStack,
    x: Int, y: Int, w: Int, h: Int,
    u: Float, v: Float,
    l: Int, c: Int, r: Int, t: Int, m: Int, b: Int
) {
    DH.drawTexture(matrices, x, y, l, t, u, v, l, t, 256, 256)
    DH.drawTexture(matrices, x + l, y, w - l - r, t, u + l, v, c, t, 256, 256)
    DH.drawTexture(matrices, x + w - r, y, r, t, u + l + c, v, r, t, 256, 256)

    DH.drawTexture(matrices, x, y + t, l, h - t - b, u, v + t, l, m, 256, 256)
    DH.drawTexture(matrices, x + l, y + t, w - l - r, h - t - b, u + l, v + t, c, m, 256, 256)
    DH.drawTexture(matrices, x + w - r, y + t, r, h - t - b, u + l + c, v + t, r, m, 256, 256)

    DH.drawTexture(matrices, x, y + h - b, l, b, u, v + t + m, l, b, 256, 256)
    DH.drawTexture(matrices, x + l, y + h - b, w - l - r, b, u + l, v + t + m, c, b, 256, 256)
    DH.drawTexture(matrices, x + w - r, y + h - b, r, b, u + l + c, v + t + m, r, b, 256, 256)
}

var Pair<Inventory, Int>.stack: ItemStack
    get() = first.getStack(second)
    set(value) = first.setStack(second, value)

typealias BlockPosList = ArrayList<BlockPos>

fun BlockPosList.toTag() = mapTo(ListTag(), BlockPos::toTag)

fun BlockPosList.fromTag(tag: ListTag) {
    clear()
    tag.mapTo(this) { (it as CompoundTag).toPos() }
}
