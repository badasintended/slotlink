package badasintended.slotlink.client.compat

import badasintended.slotlink.Slotlink
import badasintended.slotlink.gui.screen.RequestScreenHandler
import badasintended.slotlink.registry.BlockRegistry
import badasintended.slotlink.registry.NetworkRegistry.CRAFT_PULL
import badasintended.slotlink.util.buf
import com.google.common.collect.Lists
import me.shedaniel.rei.api.AutoTransferHandler.Result
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import me.shedaniel.rei.plugin.DefaultPlugin
import me.shedaniel.rei.plugin.crafting.DefaultCraftingCategory
import me.shedaniel.rei.plugin.crafting.DefaultCraftingDisplay
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry.INSTANCE
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeType
import java.util.*

@Environment(EnvType.CLIENT)
class SlotlinkReiPlugin : REIPluginV0 {

    override fun getPluginIdentifier() = Slotlink.id("rei")

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(DefaultPlugin.CRAFTING, EntryStack.create(BlockRegistry.REQUEST))

        recipeHelper.registerAutoCraftingHandler r@{ context ->
            val container = context.container
            if (container !is RequestScreenHandler) return@r Result.createNotApplicable()

            val display = context.recipe

            if (display !is DefaultCraftingDisplay) return@r Result.createNotApplicable()
            if (!display.optionalRecipe.isPresent) return@r Result.createNotApplicable()

            val recipe = display.optionalRecipe.get()
            if (recipe.type != RecipeType.CRAFTING) return@r Result.createNotApplicable()

            val input = Lists.newArrayListWithCapacity<List<EntryStack>>(9)
            for (i in 0 until 9) input.add(Collections.emptyList())
            display.inputEntries.forEachIndexed { i, entry ->
                input[DefaultCraftingCategory.getSlotWithSize(display, i, 3)] = entry
            }

            if (!INSTANCE.canServerReceive(CRAFT_PULL)) return@r Result.createFailed("error.rei.not.on.server")
            if (!context.isActuallyCrafting) return@r Result.createSuccessful()

            val outside = arrayListOf<ArrayList<Item>>()
            input.forEach { entry ->
                val inside = arrayListOf<Item>()
                entry.forEach { inside.add(it.item) }
                outside.add(inside)
            }

            val buf = buf()
            buf.writeInt(outside.size)
            outside.forEach { inside ->
                buf.writeInt(inside.size)
                inside.forEach { item ->
                    buf.writeItemStack(ItemStack(item))
                }
            }

            context.minecraft.openScreen(context.containerScreen)

            container.pullInput(outside)
            INSTANCE.sendToServer(CRAFT_PULL, buf)

            return@r Result.createSuccessful()
        }
    }

}
