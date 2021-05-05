package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.client.gui.widget.ButtonWidget
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.init.Packets
import badasintended.slotlink.screen.TransferScreenHandler
import badasintended.slotlink.util.next
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
class TransferScreen(h: TransferScreenHandler, inventory: PlayerInventory, title: Text) :
    LinkScreen<TransferScreenHandler>(h, inventory, title) {

    private var side = handler.side
    private var redstone = handler.redstone

    override fun init() {
        super.init()

        val x = x + 7
        val y = y + titleY + 11

        addButton(ButtonWidget(x + 6 * 18 + 4, y + 2, 14, 14)).apply {
            u = { 214 }
            v = { redstone.ordinal * 14 }
            onPressed = {
                redstone = redstone.next()
                sync()
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("redstone.$redstone"), x, y)
            }
        }

        addButton(ButtonWidget(x + 6 * 18 + 4, y + 38, 14, 14)).apply {
            u = { 186 }
            v = { side.ordinal * 14 }
            onPressed = {
                side = side.next()
                sync()
            }
            onHovered = { matrices, x, y ->
                renderTooltip(matrices, tl("side.$side"), x, y)
            }
        }
    }

    override fun sync() {
        super.sync()
        c2s(Packets.TRANSFER_SETTINGS) {
            writeVarInt(handler.syncId)
            writeVarInt(redstone.ordinal)
            writeVarInt(side.id)
        }
    }

}
