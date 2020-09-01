package badasintended.slotlink.registry

import badasintended.slotlink.block.entity.*
import badasintended.slotlink.gui.screen.RequestScreenHandler
import badasintended.slotlink.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.*
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object NetworkRegistry {

    val REQUEST_SAVE = modId("request_save")
    val REMOTE_SAVE = modId("remote_save")
    val CRAFT_ONCE = modId("craft_once")
    val CRAFT_STACK = modId("craft_stack")
    val CRAFT_CLEAR = modId("craft_clear")
    val CRAFT_PULL = modId("craft_pull")
    val LINK_WRITE = modId("link_write")
    val TRANSFER_WRITE = modId("transfer_write")
    val REQUEST_INIT_SERVER = modId("request_init_server")

    val REQUEST_REMOVE = modId("request_remove")
    val REQUEST_CURSOR = modId("request_cursor")
    val REQUEST_INIT_CLIENT = modId("request_init_client")

    fun initMain() {
        rS(REQUEST_SAVE) { context, buf ->
            val pos = buf.readBlockPos()
            val sort = buf.readInt()

            context.taskQueue.execute {
                val blockEntity = context.player.world.getBlockEntity(pos)

                if (blockEntity is RequestBlockEntity) {
                    blockEntity.lastSort = sort
                    blockEntity.markDirty()
                }
            }
        }

        rS(REMOTE_SAVE) { context, buf ->
            val offHand = buf.readBoolean()
            val sort = buf.readInt()

            context.taskQueue.execute {
                val stack = if (offHand) context.player.offHandStack else context.player.mainHandStack
                stack.orCreateTag.putInt("lastSort", sort)
            }
        }

        rS(CRAFT_ONCE) { context, _ ->
            context.taskQueue.execute {
                (context.player.currentScreenHandler as RequestScreenHandler).craftOnce()
            }
        }

        rS(CRAFT_STACK) { context, _ ->
            context.taskQueue.execute {
                (context.player.currentScreenHandler as RequestScreenHandler).craftStack()
            }
        }

        rS(CRAFT_CLEAR) { context, _ ->
            context.taskQueue.execute {
                (context.player.currentScreenHandler as RequestScreenHandler).clearCraft()
            }
        }

        rS(CRAFT_PULL) { context, buf ->
            val outside = arrayListOf<ArrayList<Item>>()

            for (i in 0 until buf.readInt()) {
                val inside = arrayListOf<Item>()
                for (j in 0 until buf.readInt()) {
                    inside.add(buf.readItemStack().item)
                }
                outside.add(inside)
            }

            context.taskQueue.execute {
                (context.player.currentScreenHandler as RequestScreenHandler).pullInput(outside)
            }
        }

        rS(LINK_WRITE) { context, buf ->
            val pos = buf.readBlockPos()
            val priority = buf.readVarInt()
            val isBlackList = buf.readBoolean()
            val filter = buf.readInventory()

            context.taskQueue.execute {
                val blockEntity = context.player.world.getBlockEntity(pos)
                if (blockEntity is LinkCableBlockEntity) {
                    blockEntity.priority = priority
                    blockEntity.isBlackList = isBlackList
                    blockEntity.filter = filter
                    blockEntity.markDirty()
                }
            }
        }

        rS(TRANSFER_WRITE) { context, buf ->
            val pos = buf.readBlockPos()
            val priority = buf.readVarInt()
            val isBlackList = buf.readBoolean()
            val filter = buf.readInventory()
            val side = Direction.byId(buf.readVarInt())
            val redstone = RedstoneMode.of(buf.readVarInt())

            context.taskQueue.execute {
                val blockEntity = context.player.world.getBlockEntity(pos)
                if (blockEntity is TransferCableBlockEntity) {
                    blockEntity.redstone = redstone
                    blockEntity.priority = priority
                    blockEntity.side = side
                    blockEntity.isBlackList = isBlackList
                    blockEntity.filter = filter
                    blockEntity.markDirty()
                }
            }
        }

        rS(REQUEST_INIT_SERVER) { context, buf ->
            val syncId = buf.readVarInt()

            context.taskQueue.execute {
                val screenHandler = context.player.currentScreenHandler
                if (screenHandler.syncId == syncId) if (screenHandler is RequestScreenHandler) screenHandler.init()
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun initClient() {
        rC(REQUEST_REMOVE) { context, buf ->
            val deletedInv = buf.readIntArray()

            context.taskQueue.execute {
                val screenHandler = context.player.currentScreenHandler
                if (screenHandler is RequestScreenHandler) {
                    screenHandler.linkedSlots.removeIf { it.inventoryNumber in deletedInv }
                }
            }
        }

        rC(REQUEST_CURSOR) { context, buf ->
            val stack = buf.readItemStack()

            context.taskQueue.execute {
                context.player.inventory.cursorStack = stack
            }
        }

        rC(REQUEST_INIT_CLIENT) { context, buf ->
            val id = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == id) if (handler is RequestScreenHandler) {
                    handler.init()
                }
            }
        }
    }

    private fun rS(id: Identifier, function: (PacketContext, PacketByteBuf) -> Unit) {
        ServerSidePacketRegistry.INSTANCE.register(id, PacketConsumer(function))
    }

    @Environment(EnvType.CLIENT)
    private fun rC(id: Identifier, function: (PacketContext, PacketByteBuf) -> Unit) {
        ClientSidePacketRegistry.INSTANCE.register(id, PacketConsumer(function))
    }

}
