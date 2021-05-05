package badasintended.slotlink.client.compat

import badasintended.slotlink.client.gui.screen.reiSearchHandler
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Packets.APPLY_RECIPE
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.modId
import me.shedaniel.rei.api.AutoTransferHandler.Result.createNotApplicable
import me.shedaniel.rei.api.AutoTransferHandler.Result.createSuccessful
import me.shedaniel.rei.api.BuiltinPlugin
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.REIHelper
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.recipe.RecipeType

@Environment(EnvType.CLIENT)
class SlotlinkReiPlugin : REIPluginV0 {

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
                    writeVarInt(handler.syncId)
                    writeIdentifier(recipe.id)
                }
                return@r createSuccessful()
            }
            return@r createNotApplicable()
        }

        reiSearchHandler = { REIHelper.getInstance().searchTextField?.text = it }
    }

}
