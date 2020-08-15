package badasintended.slotlink.gui.screen

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerType
import sbinnery.common.handler.BaseScreenHandler

abstract class ModScreenHandler(
    syncId: Int, playerInventory: PlayerInventory
) : BaseScreenHandler(null, syncId, playerInventory) {

    val player: PlayerEntity = playerInventory.player

    abstract override fun getType(): ScreenHandlerType<*>

}
