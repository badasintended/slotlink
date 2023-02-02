package badasintended.slotlink.compat.recipe

import badasintended.slotlink.client.gui.screen.FilterScreen
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.widget.FilterSlotWidget
import badasintended.slotlink.client.gui.widget.bounds
import badasintended.slotlink.screen.RequestScreenHandler
import dev.architectury.event.CompoundEventResult
import java.util.stream.Stream
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.REIRuntime
import me.shedaniel.rei.api.client.gui.drag.DraggableStack
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor
import me.shedaniel.rei.api.client.gui.drag.DraggableStackVisitor.BoundsProvider
import me.shedaniel.rei.api.client.gui.drag.DraggedAcceptorResult
import me.shedaniel.rei.api.client.gui.drag.DraggingContext
import me.shedaniel.rei.api.client.plugins.REIClientPlugin
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry
import me.shedaniel.rei.api.client.registry.screen.ClickArea.Result.fail
import me.shedaniel.rei.api.client.registry.screen.ClickArea.Result.success
import me.shedaniel.rei.api.client.registry.screen.DisplayBoundsProvider
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler.Result.createNotApplicable
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler.Result.createSuccessful
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry
import me.shedaniel.rei.api.common.entry.EntryIngredient
import me.shedaniel.rei.api.common.plugins.PluginManager
import me.shedaniel.rei.api.common.registry.ReloadStage
import me.shedaniel.rei.api.common.util.EntryStacks
import me.shedaniel.rei.plugin.common.BuiltinPlugin
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay
import net.minecraft.client.gui.screen.Screen
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType

@Suppress("unused")
class ReiRecipeViewer : RecipeViewer, REIClientPlugin {

    override val modName = "REI"
    override val textureV = 52

    override fun search(query: String) {
        REIRuntime.getInstance().searchTextField?.text = query
    }

    override val isDraggingStack: Boolean
        get() {
            val overlay = REIRuntime.getInstance().overlay.orElse(null) ?: return false
            return overlay.draggingContext.isDraggingStack
        }

    override fun registerCategories(registry: CategoryRegistry) {
        workstations { list ->
            registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryIngredient.of(list.map(EntryStacks::of)))
        }
    }

    override fun registerTransferHandlers(registry: TransferHandlerRegistry) {
        registry.register r@{ ctx ->
            val handler = ctx.menu
            val display = ctx.display
            if (handler is RequestScreenHandler && display is DefaultCraftingDisplay<*> && display.optionalRecipe.isPresent) {
                val recipe = display.optionalRecipe.get()
                if (recipe.type != RecipeType.CRAFTING) return@r createNotApplicable()

                if (!ctx.isActuallyCrafting) return@r createSuccessful()

                applyRecipe(ctx.containerScreen, handler, recipe.id)
                return@r createSuccessful()
            }
            return@r createNotApplicable()
        }
    }

    override fun registerScreens(registry: ScreenRegistry) {
        registry.registerFocusedStack r@{ screen, _ ->
            if (screen is RequestScreen<*>) {
                hoveredStack(screen) {
                    return@r CompoundEventResult.interruptTrue(EntryStacks.of(it.stack))
                }
            }
            CompoundEventResult.pass()
        }

        registry.registerClickArea(RequestScreen::class.java) {
            val screen = it.screen
            val mouse = it.mousePosition
            if (screen.craftingGrid
                && screen.arrowX < mouse.x
                && mouse.x <= (screen.arrowX + ARROW_WIDTH)
                && screen.arrowY < mouse.y
                && mouse.y <= (screen.arrowY + ARROW_HEIGHT)
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
                return screen.bounds(::Rectangle)
            }
        })

        registry.registerDraggableStackVisitor(object : DraggableStackVisitor<FilterScreen<*>> {
            override fun <R : Screen> isHandingScreen(screen: R) = screen is FilterScreen<*>

            override fun acceptDraggedStack(
                context: DraggingContext<FilterScreen<*>>,
                stack: DraggableStack
            ): DraggedAcceptorResult {
                val item = stack.stack.value as? ItemStack ?: return DraggedAcceptorResult.PASS
                val pos = context.currentPosition ?: return DraggedAcceptorResult.PASS
                val slot = context.screen.hoveredElement(pos.x.toDouble(), pos.y.toDouble()).orElse(null)
                    as? FilterSlotWidget ?: return DraggedAcceptorResult.PASS

                slot.setStack(item)
                return DraggedAcceptorResult.CONSUMED
            }

            override fun getDraggableAcceptingBounds(
                context: DraggingContext<FilterScreen<*>>,
                stack: DraggableStack
            ): Stream<BoundsProvider> {
                return if (stack.stack.value !is ItemStack) Stream.empty()
                else Stream.of(BoundsProvider.ofRectangles(context.screen.filterSlots.map { it.bounds(::Rectangle) }))
            }
        })
    }

    override fun preStage(manager: PluginManager<REIClientPlugin>?, stage: ReloadStage?) {
        destroy()
    }

    override fun postStage(manager: PluginManager<REIClientPlugin>?, stage: ReloadStage?) {
        attach()
    }

}
