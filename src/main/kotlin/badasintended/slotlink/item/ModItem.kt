package badasintended.slotlink.item

import badasintended.slotlink.init.Blocks
import badasintended.slotlink.util.modId
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

abstract class ModItem(id: String, settings: Settings = SETTINGS) : Item(settings) {

    val id = modId(id)

    companion object {

        private val GROUP = FabricItemGroupBuilder.build(modId("group")) { ItemStack(Blocks.MASTER) }

        val SETTINGS: Settings get() = Settings().group(GROUP)

    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(TranslatableText("${translationKey}.tooltip").formatted(Formatting.GRAY))
    }

}
