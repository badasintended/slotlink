package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.screen.ModScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.LiteralText
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface

@Environment(EnvType.CLIENT)
abstract class ModScreen<C : ModScreenHandler>(
    protected val c: C
) : BaseContainerScreen<C>(LiteralText(""), c, c.player) {

    protected val root: WInterface = `interface`

    init {
        root.setBlurred<WInterface>(true)
    }

}
