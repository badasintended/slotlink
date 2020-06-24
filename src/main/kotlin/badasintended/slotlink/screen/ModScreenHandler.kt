package badasintended.slotlink.screen

import net.minecraft.entity.player.PlayerEntity
import spinnery.common.container.BaseContainer
import spinnery.widget.WInterface

abstract class ModScreenHandler(
    syncId: Int,
    val player: PlayerEntity
) : BaseContainer(syncId, player.inventory) {

    protected val root: WInterface = `interface`

}
