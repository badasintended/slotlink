package badasintended.slotlink.init

import badasintended.slotlink.item.LimitedRemoteItem
import badasintended.slotlink.item.ModItem
import badasintended.slotlink.item.MultiDimRemoteItem
import badasintended.slotlink.item.UnlimitedRemoteItem
import badasintended.slotlink.util.modId
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries.ITEM
import net.minecraft.registry.Registry

@Suppress("MemberVisibilityCanBePrivate", "unused")
object Items : Initializer {

    val GROUP = FabricItemGroup.builder(modId("group"))
        .icon { ItemStack(Blocks.MASTER) }
        .entries { _, entries ->
            Blocks.BLOCKS.forEach { entries.add(ItemStack(it)) }
            ITEMS.forEach { entries.add(ItemStack(it)) }
        }
        .build()!!

    val ITEMS = arrayListOf<ModItem>()

    val MULTI_DIM_REMOTE = MultiDimRemoteItem()
    val UNLIMITED_REMOTE = UnlimitedRemoteItem()
    val LIMITED_REMOTE = LimitedRemoteItem()

    override fun main() {
        r(MULTI_DIM_REMOTE, UNLIMITED_REMOTE, LIMITED_REMOTE)
    }

    private fun r(vararg items: ModItem) {
        items.forEach {
            Registry.register(ITEM, it.id, it)
            ITEMS.add(it)
        }
    }

}
