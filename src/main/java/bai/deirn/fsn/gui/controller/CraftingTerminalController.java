package bai.deirn.fsn.gui.controller;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;

public class CraftingTerminalController extends CottonCraftingController {
    public CraftingTerminalController(RecipeType<?> recipeType, int syncId, PlayerInventory playerInventory) {
        super(recipeType, syncId, playerInventory);
    }
}
