package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Packets
import badasintended.slotlink.screen.TransferCableScreenHandler
import badasintended.slotlink.util.int
import badasintended.slotlink.util.next
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class TransferCableScreen(h: TransferCableScreenHandler, inventory: PlayerInventory, title: Text) :
    ConnectorCableScreen<TransferCableScreenHandler>(h, inventory, title) {

    private var side = handler.side
    private var redstone = handler.mode

    override fun init() {
        super.init()

        val x = x + 7
        val y = y + titleY + 11

        add(ButtonWidget(x + 6 * 18 + 4, y + 2, 14, 14)) {
            bgU = 228
            bgV = 28
            u = { 214 }
            v = { redstone.ordinal * 14 }
            tooltip = { tl("redstone.$redstone") }
            onPressed = {
                redstone = redstone.next()
                sync()
            }
        }

        add(ButtonWidget(x + 6 * 18 + 4, y + 38, 14, 14)) {
            bgU = 228
            bgV = 28
            u = { 200 }
            v = { side.ordinal * 14 }
            tooltip = { tl("side.$side") }
            onPressed = {
                side = side.next()
                sync()
            }
        }
    }

    override fun sync() {
        super.sync()
        c2s(Packets.TRANSFER_SETTINGS) {
            int(handler.syncId)
            int(redstone.ordinal)
            int(side.id)
        }
    }

}
