package badasintended.slotlink.common

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.ModBlock
import com.google.common.collect.ImmutableMap
import io.netty.buffer.Unpooled
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.block.Block
import net.minecraft.block.InventoryProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.WorldAccess
import spinnery.common.handler.BaseScreenHandler
import spinnery.common.registry.NetworkRegistry
import spinnery.widget.api.Action
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.api.WPositioned
import java.util.function.Consumer

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
    return ImmutableMap.builder<Direction, BlockPos>()
        .put(Direction.NORTH, north())
        .put(Direction.SOUTH, south())
        .put(Direction.EAST, east())
        .put(Direction.WEST, west())
        .put(Direction.UP, up())
        .put(Direction.DOWN, down())
        .build()
}

fun BlockEntity?.hasInv(): Boolean {
    if (this == null) return false
    return Inventory::class.java.isAssignableFrom(this.javaClass)
}

fun Block.isInvProvider(): Boolean {
    return InventoryProvider::class.java.isAssignableFrom(javaClass)
}

fun WorldAccess.isBlockIgnored(block: Block): Boolean {
    if (block is ModBlock) return true
    return world.tagManager.blocks().get(Slotlink.id("ignored"))!!.contains(block)
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
    if (this is ServerPlayerEntity) sendMessage(TranslatableText(key, *args), true)
}

fun PlayerEntity.chat(key: String, vararg args: Any) {
    if (this is ServerPlayerEntity) sendMessage(TranslatableText(key, *args), false)
}

/**
 * Opens a container, what else?
 */
@Suppress("DEPRECATION")
fun PlayerEntity.openScreen(id: String, function: (PacketByteBuf) -> Unit) {
    ContainerProviderRegistry.INSTANCE.openContainer(
        Slotlink.id(id), this as ServerPlayerEntity, Consumer(function)
    )
}

fun buf(): PacketByteBuf {
    return PacketByteBuf(Unpooled.buffer())
}

fun spinneryId(id: String) = Identifier("spinnery", id)

/**
 * I just want ints on my gui
 */
@Environment(EnvType.CLIENT)
fun positionOf(x: Int, y: Int, z: Int): Position =
    Position.of(x.toFloat(), y.toFloat(), z.toFloat())

@Environment(EnvType.CLIENT)
fun positionOf(anchor: WPositioned, x: Int, y: Int, z: Int = 0): Position {
    return Position.of(anchor, x.toFloat(), y.toFloat(), z.toFloat())
}

@Environment(EnvType.CLIENT)
fun sizeOf(x: Int, y: Int): Size =
    Size.of(x.toFloat(), y.toFloat())

@Environment(EnvType.CLIENT)
fun sizeOf(s: Int): Size =
    Size.of(s.toFloat())

@Environment(EnvType.CLIENT)
fun slotAction(container: BaseScreenHandler, slotN: Int, invN: Int, button: Int, action: Action, player: PlayerEntity) {
    container.onSlotAction(slotN, invN, button, action, player)
    ClientSidePacketRegistry.INSTANCE.sendToServer(
        NetworkRegistry.SLOT_CLICK_PACKET,
        NetworkRegistry.createSlotClickPacket(container.syncId, slotN, invN, button, action)
    )
}
