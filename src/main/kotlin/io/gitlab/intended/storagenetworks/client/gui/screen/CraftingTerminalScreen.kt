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
        val mainInterface = `interface`

        var position: Position = Position.of(0, 0, 0)
        var size: Size = Size.of(9 * 18 + 8, 3 * 18 + 108)

        val mainPanel = mainInterface.createChild({ WPanel() }, position, size)

        mainPanel.setParent<WAbstractWidget>(mainInterface)
        mainPanel.setOnAlign(WAbstractWidget::center)
        mainPanel.center()
        mainInterface.add(mainPanel)

        position = Position.of(mainPanel, mainPanel.width / 2 - (18 * 4.5f).toInt(), 3 * 18 + 24, 1)
        size = Size.of(18)
        WSlot.addPlayerInventory(position, size, mainInterface)

        position = Position.of(mainPanel, 4, 19, 1)
        WSlot.addArray(position, size, mainInterface, 0, CraftingTerminalInventory.INVENTORY, 9, 3)
    }

}