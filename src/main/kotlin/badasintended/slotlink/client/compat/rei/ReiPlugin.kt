package badasintended.slotlink.client.compat.rei

import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.screen.reiSearchHandler
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Packets.APPLY_RECIPE
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.id
import badasintended.slotlink.util.int
import badasintended.slotlink.util.modId
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.AutoTransferHandler.Result.createNotApplicable
import me.shedaniel.rei.api.AutoTransferHandler.Result.createSuccessful
import me.shedaniel.rei.api.BuiltinPlugin
import me.shedaniel.rei.api.DisplayHelper
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.REIHelper
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.recipe.RecipeType
import net.minecraft.util.TypedActionResult

@Environment(EnvType.CLIENT)
class ReiPlugin : REIPluginV0 {

    override fun getPluginIdentifier() = modId("rei")

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(BuiltinPlugin.CRAFTING, EntryStack.create(Blocks.REQUEST))
        recipeHelper.registerWorkingStations(
            BuiltinPlugin.CRAFTING,
            EntryStack.ofItems(listOf(Items.LIMITED_REMOTE, Items.UNLIMITED_REMOTE, Items.MULTI_DIM_REMOTE))
        )

        recipeHelper.registerAutoCraftingHandler r@{ ctx ->
            val handler = ctx.container
            val display = ctx.recipe
            if (handler is RequestScreenHandler) if (display is DefaultCraftingDisplay) if (display.optionalRecipe.isPresent) {
                val recipe = display.optionalRecipe.get()
                if (recipe.type != RecipeType.CRAFTING) return@r createNotApplicable()

                if (!ctx.isActuallyCrafting) return@r createSuccessful()

                ctx.minecraft.openScreen(ctx.containerScreen)
                c2s(APPLY_RECIPE) {
                    int(handler.syncId)
                    id(recipe.id)
                }
                return@r createSuccessful()
            }
            return@r createNotApplicable()
        }

        reiSearchHandler = { REIHelper.getInstance().searchTextField?.text = it }

        recipeHelper.registerFocusedStackProvider r@{ screen ->
            if (screen is RequestScreen<*>) {
                val element = screen.hoveredElement
                if (element is MultiSlotWidget) {
                    return@r TypedActionResult.success(EntryStack.create(element.stack))
                }
            }
            TypedActionResult.pass(EntryStack.empty())
        }

        recipeHelper.registerClickArea(
            { if (it.craftingGrid) Rectangle(it.x + 90, it.y + 49 + it.viewedHeight * 18, 22, 15) else Rectangle() },
            RequestScreen::class.java,
            BuiltinPlugin.CRAFTING
        )
    }

    override fun registerBounds(displayHelper: DisplayHelper) {
        displayHelper.registerProvider(object : DisplayHelper.DisplayBoundsProvider<RequestScreen<*>> {
            override fun getPriority() = 100f

            override fun getBaseSupportedClass() = RequestScreen::class.java

            override fun getScreenBounds(screen: RequestScreen<*>): Rectangle {
                return Rectangle(screen.x - 22, screen.y, screen.bgW + 40, screen.bgH)
            }
        })
    }

}