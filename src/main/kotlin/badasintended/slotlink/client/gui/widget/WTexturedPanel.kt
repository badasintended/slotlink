package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.common.texture
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.render.BaseRenderer
import spinnery.widget.WPanel

@Environment(EnvType.CLIENT)
class WTexturedPanel(
    private val texture: String
) : WPanel() {

    override fun draw() {
        if (isHidden) return
        BaseRenderer.drawImage(
            x.toDouble(), y.toDouble(), z.toDouble(),
            width.toDouble(), height.toDouble(),
            texture("gui/$texture")
        )
        orderedWidgets.forEach { it.draw() }
    }

}
