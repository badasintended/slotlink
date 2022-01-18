package badasintended.slotlink.init

import badasintended.slotlink.block.entity.TransferCableBlockEntity
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

    // S2C
    val UPDATE_SLOT_NUMBERS = modId("update_slot_numbers")
    val UPDATE_VIEWED_STACK = modId("update_viewed_stack")
    val UPDATE_MAX_SCROLL = modId("update_max_scroll")
    val UPDATE_CURSOR = modId("update_cursor")

    override fun main() {
        s(SORT) { server, player, buf ->
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

        s(SCROLL) { server, player, buf ->
            val syncId = buf.int
            val amount = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.scroll(amount)
                }
            }
        }

        s(MULTI_SLOT_ACTION) { server, player, buf ->
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

        s(APPLY_RECIPE) { server, player, buf ->
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

        s(CRAFTING_RESULT_SLOT_CLICK) { server, player, buf ->
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

        s(RESIZE) { server, player, buf ->
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

        s(CLEAR_CRAFTING_GRID) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.clearCraftingGrid(true)
                }
            }
        }

        s(MOVE) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.move()
                }
            }
        }

        s(RESTOCK) { server, player, buf ->
            val syncId = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.restock()
                }
            }
        }

        s(FILTER_SLOT_CLICK) { server, player, buf ->
            val syncId = buf.int
            val index = buf.int
            val button = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is FilterScreenHandler) {
                    handler.filterSlotClick(index, button)
                }
            }
        }

        s(FILTER_SETTINGS) { server, player, buf ->
            val syncId = buf.int
            val blacklist = buf.bool

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is FilterScreenHandler) {
                    handler.blacklist = blacklist
                }
            }
        }

        s(PRIORITY_SETTINGS) { server, player, buf ->
            val syncId = buf.int
            val priority = buf.int

            server.execute {
                val handler = player.currentScreenHandler
                if (handler.syncId == syncId && handler is ConnectorCableScreenHandler) {
                    handler.priority = priority
                }
            }
        }

        s(TRANSFER_SETTINGS) { server, player, buf ->
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
    }

    @Environment(EnvType.CLIENT)
    override fun client() {
        c(UPDATE_SLOT_NUMBERS) { client, buf ->
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

        c(UPDATE_CURSOR) { client, buf ->
            val stack = buf.stack

            client.execute {
                client.player!!.currentScreenHandler.cursorStack = stack
            }
        }

        c(UPDATE_MAX_SCROLL) { client, buf ->
            val syncId = buf.int
            val maxScroll = buf.int

            client.execute {
                val handler = client.player!!.currentScreenHandler
                if (handler.syncId == syncId) if (handler is RequestScreenHandler) {
                    handler.maxScroll = maxScroll
                }
            }
        }

        c(UPDATE_VIEWED_STACK) { client, buf ->
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

    private inline fun s(
        id: Identifier,
        crossinline function: (MinecraftServer, ServerPlayerEntity, PacketByteBuf) -> Unit
    ) {
        ServerPlayNetworking.registerGlobalReceiver(id) { server, player, _, buf, _ -> function(server, player, buf) }
    }

    @Environment(EnvType.CLIENT)
    private inline fun c(id: Identifier, crossinline function: (MinecraftClient, PacketByteBuf) -> Unit) {
        ClientPlayNetworking.registerGlobalReceiver(id) { client, _, buf, _ -> function(client, buf) }
    }

}
