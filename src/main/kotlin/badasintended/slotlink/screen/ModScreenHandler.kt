package badasintended.slotlink.screen

import badasintended.spinnery.common.container.BaseContainer
import badasintended.spinnery.widget.WInterface
import net.minecraft.entity.player.PlayerEntity

abstract class ModScreenHandler(
    syncId: Int,
    val player: PlayerEntity
) : BaseContainer(syncId, player.inventory) {

    protected val root: WInterface = `interface`

}
