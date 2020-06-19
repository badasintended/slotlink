package badasintended.slotlink.item

import badasintended.slotlink.Mod
import badasintended.slotlink.block.BlockRegistry
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

abstract class ModItem(settings: Settings) : Item(settings) {

    companion object {
        private val GROUP = FabricItemGroupBuilder
            .create(Mod.id("group"))
            .icon { ItemStack(BlockRegistry.MASTER) }
            .appendItems { stack -> BlockRegistry.BLOCKS.forEach { block -> stack.add(ItemStack(block)) } }
            .build()

        val settings: Settings get() = Settings().group(GROUP)
    }

}
