package io.gitlab.intended.storagenetworks.client.gui.screen

import io.gitlab.intended.storagenetworks.inventory.CraftingTerminalInventory
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.common.BaseContainerScreen
import spinnery.widget.WAbstractWidget
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.api.Position
import spinnery.widget.api.Size

@Environment(EnvType.CLIENT)
class CraftingTerminalScreen(
    container: CraftingTerminalInventory
) : BaseContainerScreen<CraftingTerminalInventory>(
    container.name, container, container.player
) {

    init {
        val root = `interface`

        var position: Position = Position.of(0, 0, 0)
        var size: Size = Size.of(9 * 18 + 8, 3 * 18 + 108)

        val panel = root.createChild({ WPanel() }, position, size)

        panel.setParent<WAbstractWidget>(root)
        panel.setOnAlign(WAbstractWidget::center)
        panel.center()
        root.add(panel)

        position = Position.of(panel, panel.width / 2 - (18 * 4.5f).toInt(), 3 * 18 + 24, 1)
        size = Size.of(18)
        WSlot.addPlayerInventory(position, size, root)

        position = Position.of(panel, 4, 19, 1)
        WSlot.addArray(position, size, root, 0, CraftingTerminalInventory.INV, 9, 3)
    }

}