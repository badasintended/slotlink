package badasintended.slotlink.compat.trinkets

import badasintended.slotlink.screen.RemoteScreenHandler
import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.Trinket
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object RemoteTrinket : Trinket {

    override fun onEquip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if (entity is Holder) {
            entity.remoteTrinketSlot = slot
        }
    }

    override fun onUnequip(stack: ItemStack, slot: SlotReference, entity: LivingEntity) {
        if (entity is Holder) {
            entity.remoteTrinketSlot = null
        }

        if (entity is PlayerEntity && entity.currentScreenHandler is RemoteScreenHandler) {
             entity.closeHandledScreen()
        }
    }

    interface Holder {

        @Suppress("INAPPLICABLE_JVM_NAME", "PropertyName")
        @get:JvmName("slotlink\$getRemoteTrinketSlot")
        @set:JvmName("slotlink\$setRemoteTrinketSlot")
        var _remoteTrinketSlot: Any?

    }

}

internal var RemoteTrinket.Holder.remoteTrinketSlot
    get() = _remoteTrinketSlot as SlotReference?
    set(value) {
        _remoteTrinketSlot = value
    }