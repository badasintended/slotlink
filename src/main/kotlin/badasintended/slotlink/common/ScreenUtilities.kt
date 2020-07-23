package badasintended.slotlink.common

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.LinkCableBlock
import badasintended.slotlink.block.MasterBlock
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import spinnery.common.handler.BaseScreenHandler
import spinnery.common.registry.NetworkRegistry.SLOT_CLICK_PACKET
import spinnery.common.registry.NetworkRegistry.createSlotClickPacket
import spinnery.widget.api.Action
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.api.WPositioned
import java.util.function.Consumer

/**
 * Write storage request related data
 * used in request block and remotes
 */
fun writeRequestData(buf: PacketByteBuf, world: World, masterPos: BlockPos) {
    val inventoryPos = HashSet<BlockPos>()

    val isMasterBlock = world.getBlockState(masterPos).block is MasterBlock
    buf.writeBoolean(isMasterBlock)
    if (isMasterBlock) {
        val masterNbt = world.getBlockEntity(masterPos)!!.toTag(CompoundTag())
        val linkCables = masterNbt.getList("linkCables", NbtType.COMPOUND)
        linkCables.forEach { linkCablePosTag ->
            linkCablePosTag as CompoundTag
            val linkCablePos = tag2Pos(linkCablePosTag)
            val linkCableBlock = world.getBlockState(linkCablePos).block

            if (linkCableBlock is LinkCableBlock) {
                val linkCableNbt = world.getBlockEntity(linkCablePos)!!.toTag(CompoundTag())
                val linkedPos =
                    tag2Pos(linkCableNbt.getCompound("linkedPos"))
                val linkedBlock = world.getBlockState(linkedPos).block

                if (linkedBlock.hasBlockEntity()) {
                    val linkedBlockEntity = world.getBlockEntity(linkedPos)
                    if (hasInventory(linkedBlockEntity)) {
                        inventoryPos.add(linkedPos)
                    }
                }
            }
        }

        buf.writeInt(inventoryPos.size)
        inventoryPos.forEach {
            buf.writeBlockPos(it)
        }
    }
}

/**
 * Opens a container, what else?
 */
fun openScreen(id: String, player: PlayerEntity, function: (PacketByteBuf) -> Unit) {
    ContainerProviderRegistry.INSTANCE.openContainer(Slotlink.id(id), player as ServerPlayerEntity, Consumer(function))
}

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
    ClientSidePacketRegistry.INSTANCE.sendToServer(
        SLOT_CLICK_PACKET, createSlotClickPacket(container.syncId, slotN, invN, button, action)
    )
}
