package badasintended.slotlink.inventory

import net.minecraft.inventory.Inventory

@Suppress("INAPPLICABLE_JVM_NAME")
interface PairInventory {

    @get:JvmName("slotlink\$getFirst")
    val first: Inventory

    @get:JvmName("slotlink\$getSecond")
    val second: Inventory

    @JvmName("slotlink\$isPart")
    fun isPart(inventory: Inventory): Boolean

}
