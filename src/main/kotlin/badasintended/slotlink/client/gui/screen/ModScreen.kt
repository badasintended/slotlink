package badasintended.slotlink.client.gui.screen

import badasintended.spinnery.client.screen.BaseContainerScreen
import badasintended.spinnery.widget.WInterface
import badasintended.spinnery.widget.WPanel
import badasintended.slotlink.screen.ModScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText

@Environment(EnvType.CLIENT)
abstract class ModScreen<C : ModScreenHandler>(
    protected val c: C
) : BaseContainerScreen<C>(LiteralText(""), c, c.player) {

    protected val root: WInterface = `interface`

    abstract val main: WPanel

    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, tickDelta: Float) {
        renderBackground(matrices)
        super.render(matrices, mouseX, mouseY, tickDelta)
    }

}
