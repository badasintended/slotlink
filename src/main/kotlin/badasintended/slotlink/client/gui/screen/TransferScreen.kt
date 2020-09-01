package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.WSlotButton
import badasintended.slotlink.gui.screen.TransferScreenHandler
import badasintended.slotlink.util.*
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class TransferScreen(c: TransferScreenHandler) : LinkScreen<TransferScreenHandler>(c) {

    init {
        main.createChild({
            WSlotButton().tlKey { c.side.tlKey() }.texture { c.side.texture() }.onClick { c.side = c.side.next() }
        }, positionOf(filterButton, 0, 18), sizeOf(14))
        main.createChild({
            WSlotButton()
                .tinted(false)
                .tlKey { c.redstone.tlKey }
                .texture { c.redstone.texture }
                .onClick { c.redstone = c.redstone.next() }
        }, positionOf(filterButton, 0, -18), sizeOf(14))
    }

}
