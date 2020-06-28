package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.WTranslatableLabel
import badasintended.slotlink.common.positionOf
import badasintended.slotlink.common.sizeOf
import badasintended.slotlink.screen.MasterScreenHandler
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class MasterScreen(c: MasterScreenHandler) : ModScreen<MasterScreenHandler>(c) {

    companion object {
        private fun tlkey(v: String) = "container.slotlink.master.$v"
    }

    init {
        val main = root.createChild(
            { WPanel() },
            positionOf(0, 0, 0),
            sizeOf(200, 100)
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        val title = main.createChild(
            { WTranslatableLabel("container.slotlink.master") },
            positionOf(main, 8, 6)
        )

        val connectedStorage = main.createChild(
            { WTranslatableLabel(tlkey("connected")) },
            positionOf(title, 0, 11)
        )

        for (i in 0 until c.totalInv) {
            val slot = main.createChild(
                { WSlot() },
                positionOf(connectedStorage, -1 + ((i % 8) * 18), 11 + ((i / 8) * 18), 1),
                sizeOf(18)
            )
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
        }

        val totalSlot = main.createChild(
            { WTranslatableLabel(tlkey("total"), c.totalSlot) },
            positionOf(connectedStorage, 0, 32)
        )

        main.createChild(
            { WTranslatableLabel(tlkey("max"), c.maxCount) },
            positionOf(totalSlot, 0, 11)
        )
    }

}
