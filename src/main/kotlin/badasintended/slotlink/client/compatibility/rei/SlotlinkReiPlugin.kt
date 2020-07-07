package badasintended.slotlink.client.compatibility.rei

import badasintended.slotlink.Mod
import badasintended.slotlink.block.BlockRegistry
import badasintended.slotlink.network.NetworkRegistry.CRAFT_PULL
import badasintended.slotlink.screen.AbstractRequestScreenHandler
import com.google.common.collect.Lists
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.IntArrayList
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
import net.minecraft.client.resource.language.I18n
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeType
import java.util.*

@Environment(EnvType.CLIENT)
class SlotlinkReiPlugin : REIPluginV0 {

    override fun getPluginIdentifier() = Mod.id("rei")

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(DefaultPlugin.CRAFTING, EntryStack.create(BlockRegistry.REQUEST))

        recipeHelper.registerAutoCraftingHandler r@{ context ->
            val container = context.container
            if (container !is AbstractRequestScreenHandler) return@r Result.createNotApplicable()

            val display = context.recipe

            if (display !is DefaultCraftingDisplay) return@r Result.createNotApplicable()
            if (!display.optionalRecipe.isPresent) return@r Result.createNotApplicable()

            val recipe = display.optionalRecipe.get()
            if (recipe.type != RecipeType.CRAFTING) return@r Result.createNotApplicable()

            if (!context.minecraft.player!!.recipeBook.contains(recipe)) return@r Result.createFailed(
                I18n.translate("error.rei.recipe.not.unlocked")
            )

            val input = Lists.newArrayListWithCapacity<List<EntryStack>>(9)
            for (i in 0 until 9) input.add(Collections.emptyList())
            display.inputEntries.forEachIndexed { i, entry ->
                input[DefaultCraftingCategory.getSlotWithSize(display, i, 3)] = entry
            }

            val stacks = arrayListOf<ItemStack>()
            container.playerSlots.filterNot { it.stack.isEmpty }.forEach { stacks.add(it.stack.copy()) }
            container.linkedSlots.filterNot { it.stack.isEmpty }.forEach { stacks.add(it.stack.copy()) }

            val notFound = IntArrayList()
            val foundAll = mutableMapOf<Int, Boolean>()
            input.forEachIndexed { i, list ->
                var foundOne = list.isEmpty()
                for (entry in list) {
                    val first = stacks.firstOrNull { it.item == entry.item }
                    if ((first != null) or (entry.isEmpty)) {
                        first?.decrement(1)
                        foundOne = true
                        break
                    }
                }
                if (!foundOne) notFound.add(i)
                foundAll[i] = foundOne
            }

            if (!foundAll.values.all { it }) return@r Result.createFailed(
                "error.rei.not.enough.materials", notFound
            )
            if (!INSTANCE.canServerReceive(CRAFT_PULL)) return@r Result.createFailed("error.rei.not.on.server")
            if (!context.isActuallyCrafting) return@r Result.createSuccessful()

            val outside = arrayListOf<ArrayList<Item>>()
            input.forEach { entry ->
                val inside = arrayListOf<Item>()
                entry.forEach { inside.add(it.item) }
                outside.add(inside)
            }

            val buf = PacketByteBuf(Unpooled.buffer())
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
