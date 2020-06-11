package io.gitlab.intended.storagenetworks.gui.screen

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import io.gitlab.intended.storagenetworks.gui.container.RequestContainer
import io.gitlab.intended.storagenetworks.gui.widget.WInventoryPanel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.client.screen.BaseContainerScreen
import spinnery.widget.WInterface
import spinnery.widget.WPanel
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
    companion object {
        const val g = 18f
        const val b = 7f
    }

    private val root: WInterface = `interface`
    //private val invPanel: WPanel

    init {

        val main = root.createChild(
            { WPanel() },
            Position.of(0f, 0f, 0f),
            Size.of(((9f * g) + (2f * b)), ((17f * g) + (2f * b)))
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)


        WSlot.addPlayerInventory(
            Position.of(main, b, ((13f * g) - 5f)),
            Size.of(g),
            main
        )

        WSlot.addArray(
            Position.of(main, ((2f * g) + b), ((9f * g) + b)),
            Size.of(g),
            main, 0, 1, 3, 3
        )

        val resultSlot = main.createChild(
            { WSlot() },
            Position.of(main, ((6f * g) + b - 3f), ((10f * g) + b - 3f)),
            Size.of(24f)
        )
        resultSlot.setInventoryNumber<WSlot>(2)
        resultSlot.setSlotNumber<WSlot>(0)

        val invPanel = main.createChild(
            { WInventoryPanel(container.slotList) },
            Position.of(main, b, b)
        )

        invPanel.init()
        root.recalculateCache()
    }

}
