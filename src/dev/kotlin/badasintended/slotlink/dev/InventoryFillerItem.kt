package badasintended.slotlink.dev

import badasintended.slotlink.item.ModItem
import net.minecraft.block.InventoryProvider
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.LiteralText
import net.minecraft.util.ActionResult
import net.minecraft.util.registry.Registry
import kotlin.random.Random
import kotlin.random.asJavaRandom

object InventoryFillerItem : Item(ModItem.SETTINGS) {

    private val random = Random.asJavaRandom()

    override fun hasGlint(stack: ItemStack?): Boolean {
        return true
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world = context.world
        val player = context.player ?: return ActionResult.FAIL

        if (world.isClient) return ActionResult.SUCCESS

        val pos = context.blockPos
        val state = world.getBlockState(pos)
        val block = state.block

        val inv = if (block is InventoryProvider) block.getInventory(state, world, pos) else world.getBlockEntity(pos) as? Inventory

        inv?.apply {
            for (i in 0 until size()) {
                val item = Registry.ITEM.getRandom(random)
                inv.setStack(i, ItemStack(item, item.maxCount))
            }

            player.sendMessage(LiteralText("Filled (${pos.x}, ${pos.y}, ${pos.z})"), true)
        }

        return ActionResult.SUCCESS
    }

}