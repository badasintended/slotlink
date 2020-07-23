package badasintended.slotlink.item

import badasintended.slotlink.Slotlink
import badasintended.slotlink.block.BlockRegistry
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.world.World

abstract class ModItem(id: String, settings: Settings = SETTINGS) : Item(settings) {

    val id = Slotlink.id(id)

    companion object {
        private val GROUP = FabricItemGroupBuilder.build(Slotlink.id("group")) { ItemStack(BlockRegistry.MASTER) }

        val SETTINGS: Settings get() = Settings().group(GROUP)
    }

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext
    ) {
        tooltip.add(LiteralText("§7").append(TranslatableText("${translationKey}.tooltip")))
    }

}
