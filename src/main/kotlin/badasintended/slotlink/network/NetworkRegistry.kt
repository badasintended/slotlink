package badasintended.slotlink.network

import badasintended.slotlink.Mod
import badasintended.slotlink.block.RequestBlock
import badasintended.slotlink.screen.AbstractRequestScreenHandler
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.PacketByteBuf

object NetworkRegistry {

    val REQUEST_SAVE = Mod.id("request_save")
    val REMOTE_SAVE = Mod.id("remote_save")
    val CRAFT_ONCE = Mod.id("craft_once")
    val CRAFT_STACK = Mod.id("craft_stack")
    val CRAFT_CLEAR =  Mod.id("craft_clear")

    fun initMain() {
        rS(REQUEST_SAVE, this::requestSave)
        rS(REMOTE_SAVE, this::remoteSave)
        rS(CRAFT_ONCE, this::craftOnce)
        rS(CRAFT_STACK, this::craftStack)
        rS(CRAFT_CLEAR, this::craftClear)
    }

    private fun rS(id: Identifier, function: (PacketContext, PacketByteBuf) -> Unit) {
        ServerSidePacketRegistry.INSTANCE.register(id, PacketConsumer(function))
    }

    private fun requestSave(context: PacketContext, buf: PacketByteBuf) {
        val pos = buf.readBlockPos()
        val sort = buf.readInt()

        context.taskQueue.execute {
            val world = context.player.world
            val block = world.getBlockState(pos).block

            if (block is RequestBlock) {
                val blockEntity = world.getBlockEntity(pos)!!
                val nbt = blockEntity.toTag(CompoundTag())
                nbt.putInt("lastSort", sort)
                blockEntity.fromTag(nbt)
                blockEntity.markDirty()
            }
        }
    }

    private fun remoteSave(context: PacketContext, buf: PacketByteBuf) {
        val offHand = buf.readBoolean()
        val sort = buf.readInt()

        context.taskQueue.execute {
            val stack = if (offHand) context.player.offHandStack else context.player.mainHandStack
            stack.orCreateTag.putInt("lastSort", sort)
        }
    }

    private fun craftOnce(context: PacketContext, buf: PacketByteBuf) {
        context.taskQueue.execute {
            (context.player.container as AbstractRequestScreenHandler).craftOnce()
        }
    }

    private fun craftStack(context: PacketContext, buf: PacketByteBuf) {
        context.taskQueue.execute {
            (context.player.container as AbstractRequestScreenHandler).craftStack()
        }
    }

    private fun craftClear(context: PacketContext, buf: PacketByteBuf) {
        context.taskQueue.execute {
            (context.player.container as AbstractRequestScreenHandler).clearCraft()
        }
    }

}
