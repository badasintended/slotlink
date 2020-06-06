package io.gitlab.intended.storagenetworks.gui.widget

import io.github.cottonmc.cotton.gui.ValidatedSlot
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.gitlab.intended.storagenetworks.gui.CraftingResultValidatedSlot
import net.minecraft.container.CraftingResultSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory

/**
 * [CraftingResultSlot] but LibGui Widget
 * @see CraftingResultValidatedSlot
 */
class WCraftingResultSlot(
    private val player: PlayerEntity,
    private val craftingInventory: CraftingInventory,
    inventory: Inventory,
    startIndex: Int
) : WItemSlot(inventory, startIndex, 1, 1, true, false) {

    /**
     * @return always false.
     * Well, of course you can't insert to the output slot...
     */
    override fun isInsertingAllowed() = false

    override fun createSlotPeer(inventory: Inventory, index: Int, x: Int, y: Int): ValidatedSlot {
        return CraftingResultValidatedSlot(player, craftingInventory, inventory, index, x, y)
    }

}
