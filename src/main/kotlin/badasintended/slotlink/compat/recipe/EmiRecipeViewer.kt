package badasintended.slotlink.compat.recipe

import badasintended.slotlink.client.gui.screen.ConnectorCableScreen
import badasintended.slotlink.client.gui.screen.FilterScreen
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.screen.TransferCableScreen
import badasintended.slotlink.client.gui.widget.bounds
import badasintended.slotlink.init.Screens
import badasintended.slotlink.screen.RequestScreenHandler
import dev.emi.emi.api.EmiApi
import dev.emi.emi.api.EmiDragDropHandler
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiPlayerInventory
import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.recipe.handler.EmiCraftContext
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.stack.EmiStackInteraction
import dev.emi.emi.api.stack.ItemEmiStack
import dev.emi.emi.api.widget.Bounds
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack

@Suppress("unused")
class EmiRecipeViewer : RecipeViewer, EmiPlugin {

    override val modName = "EMI"
    override val textureV = 108

    private var _isDraggingStack = false
    override val isDraggingStack get() = _isDraggingStack

    override fun search(query: String) {
        EmiApi.setSearchText(query)
    }

    override fun register(registry: EmiRegistry) {
        destroy()

        workstations { list ->
            registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiIngredient.of(list.map(EmiStack::of)))
        }

        registry.addRecipeHandler(Screens.REQUEST, RequestRecipeHandler())
        registry.addRecipeHandler(Screens.REMOTE, RequestRecipeHandler())

        registry.addStackProvider(RequestScreen::class.java) r@{ screen, _, _ ->
            hoveredStack(screen) {
                return@r EmiStackInteraction(EmiStack.of(it.stack))
            }
            EmiStackInteraction.EMPTY
        }

        registry.addExclusionArea(RequestScreen::class.java) { screen, consumer ->
            consumer.accept(screen.bounds(::Bounds))
        }

        registry.addDragDropHandler(FilterScreen::class.java, FilterDragDropHandler())
        registry.addDragDropHandler(ConnectorCableScreen::class.java, FilterDragDropHandler())
        registry.addDragDropHandler(TransferCableScreen::class.java, FilterDragDropHandler())

        attach()
    }

    private class RequestRecipeHandler<T : RequestScreenHandler> : EmiRecipeHandler<T> {

        override fun getInventory(screen: HandledScreen<T>): EmiPlayerInventory {
            return EmiPlayerInventory(listOf())
        }

        override fun supportsRecipe(recipe: EmiRecipe): Boolean {
            return recipe.category == VanillaEmiRecipeCategories.CRAFTING && recipe.supportsRecipeTree()
        }

        override fun canCraft(recipe: EmiRecipe, context: EmiCraftContext<T>): Boolean {
            return true
        }

        override fun craft(recipe: EmiRecipe, context: EmiCraftContext<T>): Boolean {
            applyRecipe(context.screen, context.screenHandler, recipe.id)
            return true
        }

    }

    private inner class FilterDragDropHandler<T : FilterScreen<*>> : EmiDragDropHandler.BoundsBased<T>({ screen, map ->
        screen.filterSlots.forEach { slot ->
            map.accept(slot.bounds(::Bounds)) {
                _isDraggingStack = false
                if (it is ItemEmiStack) slot.setStack(it.itemStack)
            }
        }
    }) {

        override fun render(
            screen: T, dragged: EmiIngredient,
            matrices: MatrixStack, mouseX: Int, mouseY: Int, delta: Float
        ) {
            _isDraggingStack = true
            super.render(screen, dragged, matrices, mouseX, mouseY, delta)
        }

    }

}
