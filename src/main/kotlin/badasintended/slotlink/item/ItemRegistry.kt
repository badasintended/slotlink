@file:Suppress("MemberVisibilityCanBePrivate")

package badasintended.slotlink.item

import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.ITEM

object ItemRegistry {

    val UNLIMITED_REMOTE = UnlimitedRemoteItem()
    val LIMITED_REMOTE = LimitedRemoteItem()

    fun init() {
        r(UNLIMITED_REMOTE, LIMITED_REMOTE)
    }

    private fun r(vararg items: ModItem) {
        items.forEach { Registry.register(ITEM, it.id, it) }
    }

}
