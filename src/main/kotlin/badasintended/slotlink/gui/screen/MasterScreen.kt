package badasintended.slotlink.gui.screen

import badasintended.slotlink.gui.container.MasterContainer
import badasintended.slotlink.gui.widget.WTranslatableLabel
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import spinnery.widget.WPanel
import spinnery.widget.WSlot
import spinnery.widget.api.Position
import spinnery.widget.api.Size
import spinnery.widget.WAbstractWidget as W

@Environment(EnvType.CLIENT)
class MasterScreen(c: MasterContainer) : ModScreen<MasterContainer>(c) {

    companion object {
        private fun tlkey(v: String) = "container.slotlink.master.$v"
    }

    init {
        val main = root.createChild(
            { WPanel() },
            Position.of(0f, 0f, 0f),
            Size.of(200f, 100f)
        )
        main.setParent<W>(root)
        main.setOnAlign(W::center)
        main.center()
        root.add(main)

        val title = main.createChild(
            { WTranslatableLabel("container.slotlink.master") },
            Position.of(main, 8f, 6f)
        )

        val connectedStorage = main.createChild(
            { WTranslatableLabel(tlkey("connected")) },
            Position.of(title, 0f, 11f)
        )

        for (i in 0 until c.totalInv) {
            val slot = main.createChild(
                { WSlot() },
                Position.of(connectedStorage, -1 + ((i % 8) * 18f), 11 + ((i / 8) * 18f), 1f),
                Size.of(18f)
            )
            slot.setInventoryNumber<WSlot>(1)
            slot.setSlotNumber<WSlot>(i)
        }

        val totalSlot = main.createChild(
            { WTranslatableLabel(tlkey("total"), c.totalSlot) },
            Position.of(connectedStorage, 0f, 32f)
        )

        main.createChild(
            { WTranslatableLabel(tlkey("max"), c.maxCount) },
            Position.of(totalSlot, 0f, 11f)
        )
    }

}
