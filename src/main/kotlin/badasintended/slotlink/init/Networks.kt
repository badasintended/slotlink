package badasintended.slotlink.init

import badasintended.slotlink.screen.*
import badasintended.slotlink.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.*
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object Networks : Initializer {

    val RESIZE = modId("resize")
    val SORT = modId("sort")
    val SCROLL = modId("scroll")
    val MULTI_SLOT_CLICK = modId("multi_slot_click")
    val CRAFTING_RESULT_SLOT_CLICK = modId("crafting_result_slot_click")
    val CLEAR_CRAFTING_GRID = modId("clear_crafting_grid")
    val APPLY_RECIPE = modId("apply_recipe")
    val MOVE = modId("move")
    val RESTOCK = modId("restock")
    val FILTER_SLOT_CLICK = modId("filter_slot_click")
    val LINK_SETTINGS = modId("link_cable_settings")
    val TRANSFER_SETTINGS = modId("transfer_settings")

    val UPDATE_VIEWED_STACK = modId("update_viewed_stack")
    val UPDATE_MAX_SCROLL = modId("update_stack_size")
    val UPDATE_CURSOR = modId("update_cursor")

    override fun main() {
        s(SORT) { context, buf ->
            val syncId = buf.readVarInt()
            val sort = Sort.of(buf.readVarInt())
            val filter = buf.readString(32767)

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.sort(sort, filter)
                }
            }
        }

        s(SCROLL) { context, buf ->
            val syncId = buf.readVarInt()
            val amount = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.scroll(amount)
                }
            }
        }

        s(MULTI_SLOT_CLICK) { context, buf ->
            val syncId = buf.readVarInt()
            val index = buf.readVarInt()
            val button = buf.readVarInt()
            val quickMove = buf.readBoolean()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.multiSlotClick(index, button, quickMove)
                }
            }
        }

        s(APPLY_RECIPE) { context, buf ->
            val syncId = buf.readVarInt()
            val recipeId = buf.readIdentifier()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    val recipe = context.player.world.recipeManager.get(recipeId)
                    if (recipe.isPresent) handler.applyRecipe(recipe.get())
                }
            }
        }

        s(CRAFTING_RESULT_SLOT_CLICK) { context, buf ->
            val syncId = buf.readVarInt()
            val button = buf.readVarInt()
            val quickMove = buf.readBoolean()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.craftingResultSlotClick(button, quickMove)
                }
            }
        }

        s(RESIZE) { context, buf ->
            val syncId = buf.readVarInt()
            val viewedHeight = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.resize(viewedHeight)
                }
            }
        }

        s(CLEAR_CRAFTING_GRID) { context, buf ->
            val syncId = buf.readVarInt()

            val handler = context.player.currentScreenHandler
            if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                handler.clearCraftingGrid(true)
            }
        }

        s(MOVE) { context, buf ->
            val syncId = buf.readVarInt()

            val handler = context.player.currentScreenHandler
            if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                handler.move()
            }
        }

        s(RESTOCK) { context, buf ->
            val syncId = buf.readVarInt()

            val handler = context.player.currentScreenHandler
            if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                handler.restock()
            }
        }

        s(FILTER_SLOT_CLICK) { context, buf ->
            val syncId = buf.readVarInt()
            val index = buf.readVarInt()
            val button = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is LinkScreenHandler) {
                    handler.filterSlotClick(index, button)
                }
            }
        }

        s(LINK_SETTINGS) { context, buf ->
            val syncId = buf.readVarInt()
            val priority = buf.readVarInt()
            val blacklist = buf.readBoolean()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is LinkScreenHandler) {
                    handler.priority = priority
                    handler.blacklist = blacklist
                }
            }
        }

        s(TRANSFER_SETTINGS) { context, buf ->
            val syncId = buf.readVarInt()
            val redstone = RedstoneMode.of(buf.readVarInt())
            val side = Direction.byId(buf.readVarInt())

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is TransferScreenHandler) {
                    handler.side = side
                    handler.redstone = redstone
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    override fun client() {
        c(UPDATE_CURSOR) { context, buf ->
            val stack = buf.readItemStack()

            context.taskQueue.execute {
                context.player.inventory.cursorStack = stack
            }
        }

        c(UPDATE_MAX_SCROLL) { context, buf ->
            val syncId = buf.readVarInt()
            val maxScroll = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.maxScroll = maxScroll
                }
            }
        }

        c(UPDATE_VIEWED_STACK) { context, buf ->
            val syncId = buf.readVarInt()
            val index = buf.readVarInt()
            val stack = buf.readItemStack()
            val count = buf.readVarInt()

            context.taskQueue.execute {
                val handler = context.player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.viewedStacks[index] = stack to count
                }
            }
        }
    }

    private fun s(id: Identifier, function: (PacketContext, PacketByteBuf) -> Unit) {
        ServerSidePacketRegistry.INSTANCE.register(id, PacketConsumer(function))
    }

    @Environment(EnvType.CLIENT)
    private fun c(id: Identifier, function: (PacketContext, PacketByteBuf) -> Unit) {
        ClientSidePacketRegistry.INSTANCE.register(id, PacketConsumer(function))
    }

}
