package badasintended.slotlink.common

import badasintended.slotlink.Mod
import badasintended.slotlink.block.LinkCableBlock
import badasintended.slotlink.block.MasterBlock
import net.fabricmc.fabric.api.container.ContainerProviderRegistry
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import spinnery.Spinnery
import java.util.function.Consumer

/**
 * Write storage request related data
 * used in request block and remotes
 */
fun writeRequestData(buf: PacketByteBuf, server: MinecraftServer?, dim: DimensionType, masterPos: BlockPos) {
    var totalInventory = 0
    val inventoryPos = HashSet<BlockPos>()

    val world = server!!.getWorld(dim)

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
                        totalInventory++
                        inventoryPos.add(linkedPos)
                    }
                }
            }
        }

        buf.writeInt(totalInventory)
        inventoryPos.forEach { buf.writeBlockPos(it) }
    }
}

/**
 * Opens a container, what else?
 */
fun openScreen(id: String, player: PlayerEntity, function: (PacketByteBuf) -> Unit) {
    ContainerProviderRegistry.INSTANCE.openContainer(Mod.id(id), player as ServerPlayerEntity, Consumer(function))
}

fun spinneryId(id: String) = Identifier(Spinnery.MOD_ID, id)
