package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.screen.ModScreenHandler
import net.minecraft.text.LiteralText
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface

abstract class ModScreen<C : ModScreenHandler>(
    protected val c: C
) : BaseContainerScreen<C>(LiteralText(""), c, c.player) {

    protected val root: WInterface = `interface`

    init {
        root.setBlurred<WInterface>(true)
    }

}
