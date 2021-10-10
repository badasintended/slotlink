package badasintended.slotlink.util

import badasintended.slotlink.mixin.CraftingScreenHandlerAccessor
import badasintended.slotlink.mixin.HandledScreenAccessor
import badasintended.slotlink.mixin.RecipeManagerAccessor
import badasintended.slotlink.mixin.TextFieldWidgetAccessor
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.inventory.Inventory
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.RecipeType
import net.minecraft.screen.CraftingScreenHandler
import net.minecraft.util.Identifier

inline val CraftingScreenHandler.input get() = (this as CraftingScreenHandlerAccessor).input
inline val CraftingScreenHandler.result get() = (this as CraftingScreenHandlerAccessor).result

inline var TextFieldWidget.focusedTicks
    get() = (this as TextFieldWidgetAccessor).focusedTicks
    set(value) = (this as TextFieldWidgetAccessor).run { focusedTicks = value }

inline val HandledScreen<*>.x get() = (this as HandledScreenAccessor).x
inline val HandledScreen<*>.y get() = (this as HandledScreenAccessor).y
inline val HandledScreen<*>.backgroundWidth get() = (this as HandledScreenAccessor).backgroundWidth
inline val HandledScreen<*>.backgroundHeight get() = (this as HandledScreenAccessor).backgroundHeight

inline val RecipeManager.recipes: MutableMap<RecipeType<*>, MutableMap<Identifier, Recipe<*>>>
    get() = (this as RecipeManagerAccessor).recipes

fun <C : Inventory, T : Recipe<C>> RecipeManager.callGetAllOfType(type: RecipeType<T>): MutableMap<Identifier, Recipe<C>> =
    (this as RecipeManagerAccessor).callGetAllOfType(type)
