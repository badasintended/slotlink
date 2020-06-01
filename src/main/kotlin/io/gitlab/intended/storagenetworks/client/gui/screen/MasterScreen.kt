package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.inventory.MasterInventory
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.BaseContainerScreen
import spinnery.widget.WAbstractWidget
import spinnery.widget.WPanel
import spinnery.widget.api.Position
import spinnery.widget.api.Size

@Environment(EnvType.CLIENT)
class MasterScreen(
    container: MasterInventory
) : BaseContainerScreen<MasterInventory>(
    container.name, container, container.player
) {

    init {
        val root = `interface`

        val position = Position.of(0, 0, 0)
        val size = Size.of(200, 400)

        val panel = root.createChild({ WPanel() }, position, size)

        panel.setParent<WAbstractWidget>(root)
        panel.setOnAlign(WAbstractWidget::center)
        panel.center()

        root.add(panel)
    }

}