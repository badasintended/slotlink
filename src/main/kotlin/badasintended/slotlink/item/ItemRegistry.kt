@file:Suppress("MemberVisibilityCanBePrivate")

package badasintended.slotlink.item

import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.ITEM

object ItemRegistry {

    val REMOTE = RemoteItem()

    fun init() {
        r(REMOTE)
    }

    private fun r(vararg items: ModItem) {
        items.forEach { Registry.register(ITEM, it.id, it) }
    }

}
