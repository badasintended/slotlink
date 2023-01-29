package badasintended.slotlink.item

import badasintended.slotlink.util.modId
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.world.World

abstract class ModItem(id: String, settings: Settings = SETTINGS) : Item(settings) {

    val id = modId(id)

    companion object {

        val SETTINGS: Settings get() = Settings()

    }

    override fun appendTooltip(stack: ItemStack, world: World?, tooltip: MutableList<Text>, context: TooltipContext) {
        tooltip.add(Text.translatable("${translationKey}.tooltip").formatted(Formatting.GRAY))
    }

}
