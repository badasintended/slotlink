package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.container.MasterContainer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.BaseContainerScreen
import spinnery.widget.WPanel
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class MasterScreen(
    container: MasterContainer
) : BaseContainerScreen<MasterContainer>(
    container.name, container, container.player
) {

    init {
        val root = `interface`

        val position = Position.of(0, 0, 0)
        val size = Size.of(200, 400)

        val panel = root.createChild({ WPanel() }, position, size)

        panel.setLabel<W>(container.name)
        panel.setParent<W>(root)
        panel.setOnAlign(W::center)
        panel.center()

        root.add(panel)
    }

}
