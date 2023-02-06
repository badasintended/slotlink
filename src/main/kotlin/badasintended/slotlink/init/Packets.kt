package badasintended.slotlink.init

import badasintended.slotlink.block.entity.TransferCableBlockEntity
import badasintended.slotlink.item.RemoteItem
import badasintended.slotlink.recipe.fastRecipeManager
import badasintended.slotlink.screen.ConnectorCableScreenHandler
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.screen.TransferCableScreenHandler
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.enum
import badasintended.slotlink.util.id
import badasintended.slotlink.util.int
import badasintended.slotlink.util.item
import badasintended.slotlink.util.modId
import badasintended.slotlink.util.nbt
import badasintended.slotlink.util.stack
import badasintended.slotlink.util.string
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

object Packets : Initializer {

    // C2S
    val RESIZE = modId("resize")
    val SORT = modId("sort")
    val SCROLL = modId("scroll")
    val MULTI_SLOT_ACTION = modId("multi_slot_action")
    val CRAFTING_RESULT_SLOT_CLICK = modId("crafting_result_slot_click")
    val CLEAR_CRAFTING_GRID = modId("clear_crafting_grid")
    val APPLY_RECIPE = modId("apply_recipe")
    val MOVE = modId("move")
    val RESTOCK = modId("restock")
    val FILTER_SLOT_CLICK = modId("filter_slot_click")
    val FILTER_SETTINGS = modId("filter_settings")
    val PRIORITY_SETTINGS = modId("priority_settings")
    val TRANSFER_SETTINGS = modId("transfer_settings")
    val OPEN_REMOTE = modId("open_remote")

    // S2C
    val UPDATE_SLOT_NUMBERS = modId("update_slot_numbers")
    val UPDATE_VIEWED_STACK = modId("update_viewed_stack")
    val UPDATE_MAX_SCROLL = modId("update_max_scroll")
    val UPDATE_CURSOR = modId("update_cursor")

    override fun main() {
        registerServerReceiver(SORT) { server, player, buf ->
            val syncId = buf.int
            val sort = buf.enum<RequestScreenHandler.SortMode>()
            val filter = buf.string

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.scheduleSort(sort, filter)
                }
            }
        }

        registerServerReceiver(SCROLL) { server, player, buf ->
            val syncId = buf.int
            val amount = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.scroll(amount)
                }
            }
        }

        registerServerReceiver(MULTI_SLOT_ACTION) { server, player, buf ->
            val syncId = buf.int
            val index = buf.int
            val button = buf.int
            val type = buf.enum<SlotActionType>()

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.multiSlotAction(index, button, type)
                }
            }
        }

        registerServerReceiver(APPLY_RECIPE) { server, player, buf ->
            val syncId = buf.int
            val recipeId = buf.id

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    val recipe = player.world.fastRecipeManager.get(recipeId)
                    if (recipe.isPresent) handler.applyRecipe(recipe.get())
                }
            }
        }

        registerServerReceiver(CRAFTING_RESULT_SLOT_CLICK) { server, player, buf ->
            val syncId = buf.int
            val button = buf.int
            val quickMove = buf.bool

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.craftingResultSlotClick(button, quickMove)
                }
            }
        }

        registerServerReceiver(RESIZE) { server, player, buf ->
            val syncId = buf.int
            val viewedHeight = buf.int
            val showCraftingGrid = buf.bool

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.resize(viewedHeight, showCraftingGrid)
                }
            }
        }

        registerServerReceiver(CLEAR_CRAFTING_GRID) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.clearCraftingGrid(true)
                }
            }
        }

        registerServerReceiver(MOVE) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.move()
                }
            }
        }

        registerServerReceiver(RESTOCK) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.restock()
                }
            }
        }

        registerServerReceiver(FILTER_SLOT_CLICK) { server, player, buf ->
            val syncId = buf.int
            val index = buf.int
            val stack = buf.stack
            val matchNbt = buf.bool

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is FilterScreenHandler) {
                    handler.filterSlotClick(index, stack, matchNbt)
                }
            }
        }

        registerServerReceiver(FILTER_SETTINGS) { server, player, buf ->
            val syncId = buf.int
            val blacklist = buf.bool

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is FilterScreenHandler) {
                    handler.blacklist = blacklist
                }
            }
        }

        registerServerReceiver(PRIORITY_SETTINGS) { server, player, buf ->
            val syncId = buf.int
            val priority = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is ConnectorCableScreenHandler) {
                    handler.priority = priority
                }
            }
        }

        registerServerReceiver(TRANSFER_SETTINGS) { server, player, buf ->
            val syncId = buf.int
            val redstone = TransferCableBlockEntity.Mode.of(buf.int)
            val side = Direction.byId(buf.int)

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is TransferCableScreenHandler) {
                    handler.side = side
                    handler.mode = redstone
                }
            }
        }

        registerServerReceiver(OPEN_REMOTE) { server, player, buf ->
            val slot = buf.int

            server.execute {
                val stack = player.inventory.getStack(slot)
                val item = stack.item
                if (item is RemoteItem) {
                    item.use(player.world, player, stack, slot)
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    override fun client() {
        registerClientReceiver(UPDATE_SLOT_NUMBERS) { client, buf ->
            val syncId = buf.int
            val total = buf.int
            val filled = buf.int

            client.execute {
                val handler = client.player!!.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.totalSlotSize = total
                    handler.filledSlotSize = filled
                }
            }
        }

        registerClientReceiver(UPDATE_CURSOR) { client, buf ->
            val stack = buf.stack

            client.execute {
                client.player!!.currentScreenHandler.cursorStack = stack
            }
        }

        registerClientReceiver(UPDATE_MAX_SCROLL) { client, buf ->
            val syncId = buf.int
            val maxScroll = buf.int

            client.execute {
                val handler = client.player!!.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.maxScroll = maxScroll
                }
            }
        }

        registerClientReceiver(UPDATE_VIEWED_STACK) { client, buf ->
            val syncId = buf.int
            val index = buf.int
            val item = buf.item
            val nbt = buf.nbt
            val count = buf.int

            client.execute {
                val handler = client.player!!.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.itemViews[index].update(item, nbt, count)
                }
            }
        }
    }

    inline fun registerServerReceiver(
        id: Identifier,
        crossinline function: (MinecraftServer, ServerPlayerEntity, PacketByteBuf) -> Unit
    ) {
        ServerPlayNetworking.registerGlobalReceiver(id) { server, player, _, buf, _ -> function(server, player, buf) }
    }

    @Environment(EnvType.CLIENT)
    private inline fun registerClientReceiver(id: Identifier, crossinline function: (MinecraftClient, PacketByteBuf) -> Unit) {
        ClientPlayNetworking.registerGlobalReceiver(id) { client, _, buf, _ -> function(client, buf) }
    }

}
