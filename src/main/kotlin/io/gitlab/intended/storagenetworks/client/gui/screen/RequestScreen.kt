package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.container.RequestContainer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.BaseContainerScreen
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class RequestScreen(
    container: RequestContainer
) : BaseContainerScreen<RequestContainer>(
    container.name, container, container.player
) {

    init {
        val root = `interface`

        var position: Position = Position.of(0, 0, 0)
        var size: Size = Size.of(9 * 18 + 8, 3 * 18 + 108)

        val panel = root.createChild({ WPanel() }, position, size)

        panel.setLabel<W>(container.name)
        panel.setParent<W>(root)
        panel.setOnAlign(W::center)
        panel.center()
        root.add(panel)

        position = Position.of(panel, panel.width / 2 - (18 * 4.5f).toInt(), 3 * 18 + 24, 1)
        size = Size.of(18)
        WSlot.addPlayerInventory(position, size, root)

        position = Position.of(panel, 4, 19, 1)
        WSlot.addArray(position, size, root, 0, RequestContainer.INV, 9, 3)
    }

}
