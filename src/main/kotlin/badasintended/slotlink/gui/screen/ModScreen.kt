package badasintended.slotlink.gui.screen

import badasintended.slotlink.gui.container.ModContainer
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface

abstract class ModScreen<C : ModContainer>(
    protected val c: C
) : BaseContainerScreen<C>(c.block.name, c, c.player) {

    protected val root: WInterface = `interface`

    init {
        root.setBlurred<WInterface>(true)
    }

}
