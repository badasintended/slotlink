package badasintended.slotlink.common

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.LinkCableBlock
import badasintended.slotlink.block.MasterBlock
import com.google.common.collect.ImmutableMap
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.util.NbtType
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
import net.minecraft.world.World
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

fun PacketByteBuf.writeReqData(world: World, masterPos: BlockPos) {
    val inventoryPos = HashSet<BlockPos>()

    writeIdentifier(world.registryKey.value)

    val isMasterBlock = world.getBlockState(masterPos).block is MasterBlock
    writeBoolean(isMasterBlock)
    if (isMasterBlock) {
        val masterNbt = world.getBlockEntity(masterPos)!!.toTag(CompoundTag())
        val linkCables = masterNbt.getList("linkCables", NbtType.COMPOUND)
        linkCables.forEach { linkCablePosTag ->
            linkCablePosTag as CompoundTag
            val linkCablePos = linkCablePosTag.toPos()
            val linkCableBlock = world.getBlockState(linkCablePos).block

            if (linkCableBlock is LinkCableBlock) {
                val linkCableNbt = world.getBlockEntity(linkCablePos)!!.toTag(CompoundTag())
                val linkedPos = linkCableNbt.getCompound("linkedPos").toPos()
                val linkedBlock = world.getBlockState(linkedPos).block

                if (!world.isBlockIgnored(linkedBlock)) {
                    if (linkedBlock.isInvProvider()) {
                        inventoryPos.add(linkedPos)
                    } else if (linkedBlock.hasBlockEntity()) {
                        val linkedBlockEntity = world.getBlockEntity(linkedPos)
                        if (linkedBlockEntity.hasInv()) {
                            inventoryPos.add(linkedPos)
                        }
                    }
                }
            }
        }

        writeInt(inventoryPos.size)
        inventoryPos.forEach {
            writeBlockPos(it)
        }
    }
}

/**
 * Opens a container, what else?
 */
fun openScreen(id: String, player: PlayerEntity, function: (PacketByteBuf) -> Unit) {
    ContainerProviderRegistry.INSTANCE.openContainer(
        Slotlink.id(id), player as ServerPlayerEntity, Consumer(function)
    )
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
