package badasintended.slotlink.dev

import badasintended.slotlink.util.modId
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

@Suppress("unused")
object SlotlinkDev {

    fun main() {
        Registry.register(Registries.ITEM, modId("storage_filler"), StorageFillerItem)
    }

}