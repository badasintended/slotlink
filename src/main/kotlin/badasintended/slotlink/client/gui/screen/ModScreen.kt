package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.gui.screen.ModScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.text.LiteralText
import sbinnery.client.screen.BaseHandledScreen
import sbinnery.widget.WInterface

@Environment(EnvType.CLIENT)
abstract class ModScreen<C : ModScreenHandler>(
    protected val c: C
) : BaseHandledScreen<C>(LiteralText(""), c, c.player) {

    protected val root: WInterface = `interface`

}
