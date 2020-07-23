package badasintended.slotlink.screen

import net.minecraft.entity.player.PlayerEntity
import spinnery.common.handler.BaseScreenHandler
import spinnery.widget.WInterface

abstract class ModScreenHandler(
    syncId: Int,
    val player: PlayerEntity
) : BaseScreenHandler(syncId, player.inventory) {

    protected val root: WInterface = `interface`

}
