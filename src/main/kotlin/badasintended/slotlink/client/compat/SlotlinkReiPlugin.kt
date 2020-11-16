package badasintended.slotlink.client.compat

import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.init.Networks.APPLY_RECIPE
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.buf
import badasintended.slotlink.util.c2s
import badasintended.slotlink.util.modId
import me.shedaniel.rei.api.AutoTransferHandler.Result.createFailed
import me.shedaniel.rei.api.AutoTransferHandler.Result.createNotApplicable
import me.shedaniel.rei.api.AutoTransferHandler.Result.createSuccessful
import me.shedaniel.rei.api.BuiltinPlugin
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
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

                if (!INSTANCE.canServerReceive(APPLY_RECIPE)) return@r createFailed("error.rei.not.on.server")
                if (!ctx.isActuallyCrafting) return@r createSuccessful()

                val buf = buf().apply {
                    writeVarInt(handler.syncId)
                    writeIdentifier(recipe.id)
                }

                ctx.minecraft.openScreen(ctx.containerScreen)
                c2s(APPLY_RECIPE, buf)
                return@r createSuccessful()
            }
            return@r createNotApplicable()
        }

    }

}
