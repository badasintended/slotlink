package badasintended.slotlink.datagen.provider

import badasintended.slotlink.init.Blocks
import badasintended.slotlink.init.Items
import badasintended.slotlink.util.modId
import java.util.function.Consumer
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.block.Blocks as McBlocks
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder.create as shaped
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder.create as shapeless
import net.minecraft.item.Items as McItems

class RecipeProvider(dataGenerator: FabricDataOutput) : FabricRecipeProvider(dataGenerator) {

    override fun generate(exporter: Consumer<RecipeJsonProvider>) {
        shaped(RecipeCategory.MISC, Blocks.CABLE, 8)
            .pattern("SSS")
            .pattern("I I")
            .pattern("SSS")
            .input('I', tag("c:iron_ingots"))
            .input('S', McBlocks.STONE_SLAB)
            .criterion(McItems.IRON_INGOT, tag("c:iron_ingots"))
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Blocks.IMPORT_CABLE, 4)
            .pattern(" C ")
            .pattern("CHC")
            .pattern(" C ")
            .input('H', tag("c:hoppers"))
            .input('C', Blocks.CABLE)
            .criterion(Blocks.CABLE)
            .offerTo(exporter)

        shapeless(RecipeCategory.MISC, Blocks.IMPORT_CABLE)
            .input(Blocks.EXPORT_CABLE)
            .criterion(Blocks.CABLE)
            .criterion(Blocks.EXPORT_CABLE)
            .offerTo(exporter, modId("export_to_import_cable"))

        shapeless(RecipeCategory.MISC, Blocks.EXPORT_CABLE)
            .input(Blocks.IMPORT_CABLE)
            .criterion(Blocks.CABLE)
            .criterion(Blocks.IMPORT_CABLE)
            .offerTo(exporter, modId("import_to_export_cable"))

        shaped(RecipeCategory.MISC, Blocks.LINK_CABLE, 4)
            .pattern(" C ")
            .pattern("CHC")
            .pattern(" C ")
            .input('H', tag("c:wooden_chests"))
            .input('C', Blocks.CABLE)
            .criterion(Blocks.CABLE)
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Blocks.MASTER)
            .pattern("QCQ")
            .pattern("CDC")
            .pattern("QCQ")
            .input('D', tag("c:diamonds"))
            .input('Q', tag("c:quartz_blocks"))
            .input('C', Blocks.CABLE)
            .criterion(Blocks.CABLE)
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Blocks.REQUEST)
            .pattern("TCT")
            .pattern("CGC")
            .pattern("TCT")
            .input('G', tag("c:gold_ingots"))
            .input('T', McBlocks.CRAFTING_TABLE)
            .input('C', Blocks.LINK_CABLE)
            .criterion(Blocks.LINK_CABLE)
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Blocks.INTERFACE)
            .pattern("SIS")
            .pattern("IRE")
            .pattern("SES")
            .input('R', tag("c:iron_ingots"))
            .input('S', McBlocks.SMOOTH_STONE)
            .input('I', Blocks.IMPORT_CABLE)
            .input('E', Blocks.EXPORT_CABLE)
            .criterion(Blocks.IMPORT_CABLE)
            .criterion(Blocks.EXPORT_CABLE)
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Items.LIMITED_REMOTE)
            .pattern("SDS")
            .pattern("GRG")
            .pattern("SDS")
            .input('S', tag("c:redstone_dusts"))
            .input('D', tag("c:diamonds"))
            .input('G', tag("c:gold_ingots"))
            .input('R', Blocks.REQUEST)
            .criterion(Blocks.REQUEST)
            .offerTo(exporter)

        shaped(RecipeCategory.MISC, Items.UNLIMITED_REMOTE)
            .pattern("GEC")
            .pattern("PRP")
            .pattern("CEG")
            .input('E', McItems.ENDER_EYE)
            .input('P', McItems.PHANTOM_MEMBRANE)
            .input('G', McItems.GHAST_TEAR)
            .input('C', McItems.PRISMARINE_CRYSTALS)
            .input('R', Items.LIMITED_REMOTE)
            .criterion(Items.LIMITED_REMOTE)
            .offerTo(exporter)

        shapeless(RecipeCategory.MISC, Items.MULTI_DIM_REMOTE)
            .input(McItems.DRAGON_BREATH)
            .input(McItems.TOTEM_OF_UNDYING)
            .input(McItems.NETHER_STAR)
            .input(Items.UNLIMITED_REMOTE)
            .criterion(Items.UNLIMITED_REMOTE)
            .offerTo(exporter)
    }

    private fun tag(id: String): TagKey<Item> {
        return TagKey.of(RegistryKeys.ITEM, Identifier(id))
    }

    private fun <T : CraftingRecipeJsonBuilder> T.criterion(item: ItemConvertible): T {
        criterion(hasItem(item), conditionsFromItem(item))
        return this
    }

    private fun <T : CraftingRecipeJsonBuilder> T.criterion(item: ItemConvertible, tag: TagKey<Item>): T {
        criterion(hasItem(item), conditionsFromTag(tag))
        return this
    }

    override fun getRecipeIdentifier(identifier: Identifier): Identifier {
        return identifier
    }

}