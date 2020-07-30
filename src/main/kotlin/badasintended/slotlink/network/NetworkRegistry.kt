package badasintended.slotlink.network

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.entity.RequestBlockEntity
import badasintended.slotlink.inventory.DummyInventory
import badasintended.slotlink.screen.AbstractRequestScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketConsumer
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

object NetworkRegistry {

    val REQUEST_SAVE = Slotlink.id("request_save")
    val REMOTE_SAVE = Slotlink.id("remote_save")
    val CRAFT_ONCE = Slotlink.id("craft_once")
    val CRAFT_STACK = Slotlink.id("craft_stack")
    val CRAFT_CLEAR = Slotlink.id("craft_clear")
    val CRAFT_PULL = Slotlink.id("craft_pull")

    val REQUEST_SYNC = Slotlink.id("request_sync")
    val REQUEST_REMOVE = Slotlink.id("request_remove")

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
                (context.player.currentScreenHandler as AbstractRequestScreenHandler).craftOnce()
            }
        }

        rS(CRAFT_STACK) { context, _ ->
            context.taskQueue.execute {
                (context.player.currentScreenHandler as AbstractRequestScreenHandler).craftStack()
            }
        }

        rS(CRAFT_CLEAR) { context, _ ->
            context.taskQueue.execute {
                (context.player.currentScreenHandler as AbstractRequestScreenHandler).clearCraft()
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
                (context.player.currentScreenHandler as AbstractRequestScreenHandler).pullInput(outside)
            }
        }
    }

    @Environment(EnvType.CLIENT)
    fun initClient() {
        rC(REQUEST_SYNC) { context, buf ->
            /**
             * 1. inv count
             * 2. inv number
             * 3. inv size
             * 4. inv maxCount
             * 5. inv stacks
             */
            val invMap = hashMapOf<Int, Inventory>()

            for (i in 0 until buf.readInt()) {
                val num = buf.readInt()
                val inv = DummyInventory(buf.readInt())
                inv.maxCount = buf.readInt()
                for (j in 0 until inv.size()) {
                    inv.setStack(j, buf.readItemStack())
                }
                invMap[num] = inv
            }
            context.taskQueue.execute {
                (context.player.currentScreenHandler as AbstractRequestScreenHandler).createSlots(invMap)
            }
        }

        rC(REQUEST_REMOVE) { context, buf ->
            val deletedInv = buf.readIntArray()

            context.taskQueue.execute {
                val screenHandler = context.player.currentScreenHandler
                if (screenHandler is AbstractRequestScreenHandler) {
                    screenHandler.linkedSlots.removeIf { it.inventoryNumber in deletedInv }
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
