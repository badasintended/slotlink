package badasintended.slotlink.dev

import badasintended.slotlink.util.modId
import net.minecraft.util.registry.Registry

object SlotlinkDev {

    fun main() {
        Registry.register(Registry.ITEM, modId("storage_filler"), StorageFillerItem)
    }

}