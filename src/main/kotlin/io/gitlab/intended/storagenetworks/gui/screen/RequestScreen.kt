package io.gitlab.intended.storagenetworks.gui.screen

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.gui.container.RequestContainer
import io.gitlab.intended.storagenetworks.gui.widget.WInventoryPanel
import io.gitlab.intended.storagenetworks.gui.widget.WMouseArea
import io.gitlab.intended.storagenetworks.gui.widget.WTexturedPanel
import io.gitlab.intended.storagenetworks.gui.widget.WTranslatableLabel
import io.gitlab.intended.storagenetworks.texture
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface
import spinnery.widget.WSlot
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class RequestScreen(container: RequestContainer) : BaseContainerScreen<RequestContainer>(
    BlockRegistry.REQUEST.name,
    container,
    container.player
) {
    private val root: WInterface = `interface`

    init {

        val main = root.createChild(
            { WTexturedPanel(texture("gui/request")) },
            Position.of(0f, 0f, 0f),
            Size.of(176f, 310f)
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        // Storage Request title
        val title = main.createChild(
            { WTranslatableLabel("container.storagenetworks.request") },
            Position.of(main, 8f, 6f)
        )

        // Crafting label
        val craftingLabel = main.createChild(
            { WTranslatableLabel("container.crafting") },
            Position.of(title, 19f, 144f)
        )

        // Crafting Input slots
        val craftingSlots = WSlot.addArray(
            Position.of(craftingLabel, 1f, 10f),
            Size.of(18f),
            main, 0, 1, 3, 3
        )

        // Crafting Result slot
        val resultSlot = main.createChild(
            { WSlot() },
            Position.of(craftingLabel, 91f, 24f),
            Size.of(26f)
        )
        resultSlot.setInventoryNumber<WSlot>(2)
        resultSlot.setSlotNumber<WSlot>(0)

        // Player Inventory label
        val playerInvLabel = main.createChild(
            { WTranslatableLabel("container.inventory") },
            Position.of(craftingLabel, -19f, 66f)
        )

        // Player Inventory slots
        WSlot.addPlayerInventory(
            Position.of(playerInvLabel, -1f, 11f),
            Size.of(18f),
            main
        )

        // Inventory Panel (slot, scrollbar, searchbar, etc.)
        val invPanel = main.createChild(
            {
                WInventoryPanel(
                    container.slotList,
                    container.lastSort,
                    { container.saveLastSort(it) },
                    { container.isDeleted(it) })
            },
            Position.of(title, -1f, 11f)
        )
        invPanel.init()

        val playerInvArea = main.createChild(
            { WMouseArea() },
            Position.of(playerInvLabel, -1f, 11f),
            Size.of(162f, 76f)
        )
        playerInvArea.onMouseReleased = { invPanel.sort() }

        // root.recalculateCache()
    }

}
