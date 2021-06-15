package badasintended.slotlink.client.compat.rei

import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.screen.reiSearchHandler
import badasintended.slotlink.client.gui.widget.MultiSlotWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Packets.APPLY_RECIPE
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.backgroundHeight
import badasintended.slotlink.util.backgroundWidth
import badasintended.slotlink.util.id
import badasintended.slotlink.util.int
import badasintended.slotlink.util.x
import badasintended.slotlink.util.y
import dev.architectury.event.CompoundEventResult
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.screen.ClickArea.Result.fail
import me.shedaniel.rei.api.client.registry.screen.ClickArea.Result.success
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler.Result.createNotApplicable
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler.Result.createSuccessful
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry
import me.shedaniel.rei.api.common.util.EntryStacks
import me.shedaniel.rei.plugin.common.BuiltinPlugin
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.recipe.RecipeType

@Suppress("unused")
@Environment(EnvType.CLIENT)
class ReiPlugin : REIClientPlugin {

    override fun registerCategories(registry: CategoryRegistry) {
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(Blocks.REQUEST))
        registry.addWorkstations(
            BuiltinPlugin.CRAFTING,
            EntryStacks.of(Items.LIMITED_REMOTE),
            EntryStacks.of(Items.UNLIMITED_REMOTE),
            EntryStacks.of(Items.MULTI_DIM_REMOTE)
        )
    }

    override fun registerTransferHandlers(registry: TransferHandlerRegistry) {
        registry.register r@{ ctx ->
            val handler = ctx.menu
            val display = ctx.display
            if (handler is RequestScreenHandler && display is DefaultCraftingDisplay<*> && display.optionalRecipe.isPresent) {
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
    }

    override fun registerScreens(registry: ScreenRegistry) {
        registry.registerFocusedStack r@{ screen, _ ->
            if (screen is RequestScreen<*>) {
                val element = screen.hoveredElement
                if (element is MultiSlotWidget) {
                    return@r CompoundEventResult.interruptTrue(EntryStacks.of(element.stack))
                }
            }
            CompoundEventResult.pass()
        }

        registry.registerClickArea(RequestScreen::class.java) {
            val screen = it.screen
            val mouse = it.mousePosition
            if (screen.craftingGrid
                && screen.arrowX < mouse.x
                && mouse.x <= (screen.arrowX + 22)
                && screen.arrowY < mouse.y
                && mouse.y <= (screen.arrowY + 15)
            ) {
                success().category(BuiltinPlugin.CRAFTING)
            } else {
                fail()
            }
        }

        registry.registerDecider(object : DisplayBoundsProvider<RequestScreen<*>> {
            override fun getPriority() = 100.0

            override fun <R : Screen> isHandingScreen(screen: Class<R>): Boolean {
                return RequestScreen::class.java.isAssignableFrom(screen)
            }

            override fun getScreenBounds(screen: RequestScreen<*>): Rectangle {
                return Rectangle(screen.x - 22, screen.y, screen.backgroundWidth + 40, screen.backgroundHeight)
            }
        })
    }

    override fun postRegister() {
        reiSearchHandler = { REIRuntime.getInstance().searchTextField?.text = it }
    }

}
