package badasintended.slotlink.compat.recipe

import badasintended.slotlink.client.gui.screen.FilterScreen
import badasintended.slotlink.client.gui.screen.RequestScreen
import badasintended.slotlink.client.gui.widget.bounds
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.modId
import java.util.Optional
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.constants.RecipeTypes
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.handlers.IGhostIngredientHandler
import mezz.jei.api.gui.handlers.IGuiClickableArea
import mezz.jei.api.gui.handlers.IGuiContainerHandler
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.ingredients.ITypedIngredient
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeTransferRegistration
import mezz.jei.api.runtime.IClickableIngredient
import mezz.jei.api.runtime.IIngredientManager
import mezz.jei.api.runtime.IJeiRuntime
import net.minecraft.client.util.math.Rect2i
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.screen.ScreenHandlerType

private val id = modId("jei")

@JeiPlugin
@Suppress("unused")
class JeiRecipeViewer : RecipeViewer, IModPlugin {

    private lateinit var runtime: IJeiRuntime

    override val modName = "JEI"
    override val textureV = 80

    // TODO: JEI doesn't support holding modifier while dragging stacks
    override val isDraggingStack = false

    override fun getPluginUid() = id

    override fun search(query: String) {
        runtime.ingredientFilter.filterText = query
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {
        workstations { list ->
            list.forEach { registration.addRecipeCatalyst(it, RecipeTypes.CRAFTING) }
        }
    }

    override fun registerRecipeTransferHandlers(registration: IRecipeTransferRegistration) {
        registration.addRecipeTransferHandler(RequestRecipeTransferHandler, RecipeTypes.CRAFTING)
    }

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {
        registration.addGuiContainerHandler(
            RequestScreen::class.java,
            RequestGuiContainerHandler(registration.jeiHelpers.ingredientManager)
        )

        registration.addGhostIngredientHandler(FilterScreen::class.java, FilterGhostIngredientHandler())
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        runtime = jeiRuntime
        attach()
    }

    override fun onRuntimeUnavailable() {
        runtime = null!!
        destroy()
    }

    private object RequestRecipeTransferHandler : IRecipeTransferHandler<RequestScreenHandler, CraftingRecipe> {

        override fun getContainerClass(): Class<out RequestScreenHandler> {
            return RequestScreenHandler::class.java
        }

        override fun getMenuType(): Optional<ScreenHandlerType<RequestScreenHandler>> {
            return Optional.empty()
        }

        override fun getRecipeType(): RecipeType<CraftingRecipe> {
            return RecipeTypes.CRAFTING
        }

        override fun transferRecipe(
            container: RequestScreenHandler,
            recipe: CraftingRecipe,
            recipeSlots: IRecipeSlotsView,
            player: PlayerEntity,
            maxTransfer: Boolean,
            doTransfer: Boolean
        ): IRecipeTransferError? {
            if (doTransfer) {
                applyRecipe(null, container, recipe.id)
            }
            return null
        }

    }

    private class RequestGuiContainerHandler(
        private val ingredientManager: IIngredientManager
    ) : IGuiContainerHandler<RequestScreen<*>> {

        override fun getGuiExtraAreas(screen: RequestScreen<*>): MutableList<Rect2i> {
            return mutableListOf(screen.bounds(::Rect2i))
        }

        override fun getClickableIngredientUnderMouse(
            containerScreen: RequestScreen<*>,
            mouseX: Double,
            mouseY: Double
        ): Optional<IClickableIngredient<*>> {
            hoveredStack(containerScreen) { slot ->
                return ingredientManager.createTypedIngredient(VanillaTypes.ITEM_STACK, slot.stack).map { ingredient ->
                    object : IClickableIngredient<ItemStack> {
                        override fun getTypedIngredient(): ITypedIngredient<ItemStack> {
                            return ingredient
                        }

                        override fun getArea(): Rect2i {
                            return Rect2i(slot.x, slot.y, slot.width, slot.height)
                        }
                    }
                }
            }
            return Optional.empty()
        }

        override fun getGuiClickableAreas(
            containerScreen: RequestScreen<*>,
            guiMouseX: Double,
            guiMouseY: Double
        ): MutableCollection<IGuiClickableArea> {
            return mutableListOf(
                IGuiClickableArea.createBasic(
                    containerScreen.arrowX, containerScreen.arrowY,
                    ARROW_WIDTH, ARROW_HEIGHT,
                    RecipeTypes.CRAFTING
                )
            )
        }
    }

    private inner class FilterGhostIngredientHandler : IGhostIngredientHandler<FilterScreen<*>> {

        override fun <I : Any?> getTargets(
            gui: FilterScreen<*>,
            ingredient: I,
            doStart: Boolean
        ): MutableList<IGhostIngredientHandler.Target<I>> {
            return if (ingredient is ItemStack)
                gui.filterSlots.mapTo(mutableListOf()) { slot ->
                    object : IGhostIngredientHandler.Target<I> {
                        override fun getArea(): Rect2i {
                            return slot.bounds(::Rect2i)
                        }

                        override fun accept(ingredient: I) {
                            if (ingredient is ItemStack) {
                                slot.setStack(ingredient)
                            }
                        }
                    }
                }
            else mutableListOf()
        }

        override fun onComplete() {}

    }

}
