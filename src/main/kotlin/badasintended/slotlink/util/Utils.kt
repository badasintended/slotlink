package badasintended.slotlink.util

import badasintended.slotlink.Slotlink
import com.google.common.collect.ImmutableMap
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import sbinnery.common.handler.BaseScreenHandler
import sbinnery.common.registry.NetworkRegistry.SLOT_CLICK_PACKET
import sbinnery.common.registry.NetworkRegistry.createSlotClickPacket
import sbinnery.common.utility.StackUtilities
import sbinnery.widget.api.*

fun spinneryId(id: String) = Identifier("spinnery", id)

/**
 * I just want ints on my gui
 */
@Environment(EnvType.CLIENT)
fun positionOf(x: Int, y: Int, z: Int): Position = Position.of(x.toFloat(), y.toFloat(), z.toFloat())

@Environment(EnvType.CLIENT)
fun positionOf(anchor: WPositioned, x: Int, y: Int, z: Int = 0): Position {
    return Position.of(anchor, x.toFloat(), y.toFloat(), z.toFloat())
}

@Environment(EnvType.CLIENT)
fun sizeOf(x: Int, y: Int): Size = Size.of(x.toFloat(), y.toFloat())

@Environment(EnvType.CLIENT)
fun sizeOf(s: Int): Size = Size.of(s.toFloat())

@Environment(EnvType.CLIENT)
fun slotAction(container: BaseScreenHandler, slotN: Int, invN: Int, button: Int, action: Action, player: PlayerEntity) {
    container.onSlotAction(slotN, invN, button, action, player)
    c2s(SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotN, invN, button, action))
}

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

fun Direction.tlKey(): String {
    return "container.slotlink.cable.side.${asString()}"
}

fun Inventory.mergeStack(slot: Int, source: ItemStack, side: Direction) {
    var target = getStack(slot)
    while ((target.count < target.maxCount) and !source.isEmpty) {
        val one = source.copy()
        one.count = 1
        if (!isValid(slot, one)) return
        if (this is SidedInventory) if (!canInsert(slot, one, side)) return
        if (target.isEmpty) {
            setStack(slot, one)
            source.decrement(1)
            target = getStack(slot)
        } else {
            if (!StackUtilities.equalItemAndTag(source, target)) return
            target.increment(1)
            source.decrement(1)
        }
    }
}

fun PacketByteBuf.writeInventory(stacks: DefaultedList<ItemStack>) {
    writeVarInt(stacks.size)
    stacks.forEach { writeItemStack(it) }
}

fun PacketByteBuf.readInventory(): DefaultedList<ItemStack> {
    val stack = DefaultedList.ofSize(readVarInt(), ItemStack.EMPTY)
    for (i in 0 until stack.size) {
        stack[i] = readItemStack()
    }
    return stack
}

fun tex(path: String) = modId("textures/${path}.png")

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
