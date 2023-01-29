@file:Suppress("UnstableApiUsage")

package badasintended.slotlink.util

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandler

val StorageView<ItemVariant>.isEmpty
    get() = resource.isBlank || amount == 0L

val PlayerEntity.storage: PlayerInventoryStorage
    get() = PlayerInventoryStorage.of(this)

val ScreenHandler.cursorStorage: SingleSlotStorage<ItemVariant>
    get() = PlayerInventoryStorage.getCursorStorage(this)
