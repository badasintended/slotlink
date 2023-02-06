package badasintended.slotlink.compat.trinkets

import dev.emi.trinkets.api.SlotReference
import net.minecraft.item.ItemStack

internal var SlotReference.stack: ItemStack
    get() = inventory.getStack(index)
    set(value) = inventory.setStack(index, value)