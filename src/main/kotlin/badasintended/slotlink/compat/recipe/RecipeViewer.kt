package badasintended.slotlink.compat.recipe

import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Packets
import badasintended.slotlink.util.id
import badasintended.slotlink.util.int
import badasintended.slotlink.util.log
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.Identifier

internal const val ARROW_WIDTH = 22
internal const val ARROW_HEIGHT = 15

interface RecipeViewer {

    companion object {

        @Suppress("ObjectPropertyName")
        private var _instance: RecipeViewer? = null
        val instance get() = _instance

    }

    val modName: String

    val textureV: Int

    val isDraggingStack: Boolean

    fun search(query: String)

    fun attach() {
        _instance = this
        log.info("[slotlink] Attached recipe viewer compat for $modName")
    }

    fun destroy() {
        _instance = null
    }

}

internal fun applyRecipe(screen: HandledScreen<*>?, handler: ScreenHandler, recipeId: Identifier?) {
    if (screen != null) MinecraftClient.getInstance().setScreen(screen)
    if (recipeId != null) {
        c2s(Packets.APPLY_RECIPE) {
            int(handler.syncId)
            id(recipeId)
        }
    }
}

internal inline fun workstations(action: (List<ItemStack>) -> Unit) {
    action(listOf(ItemStack(Blocks.REQUEST)))
    action(
        listOf(
            ItemStack(Items.LIMITED_REMOTE),
            ItemStack(Items.UNLIMITED_REMOTE),
            ItemStack(Items.MULTI_DIM_REMOTE)
        )
    )
}

internal inline fun hoveredStack(screen: RequestScreen<*>, action: (MultiSlotWidget) -> Unit) {
    val element = screen.hoveredElement
    if (element is MultiSlotWidget) {
        action(element)
    }
}
