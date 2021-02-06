package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.init.Packets
import badasintended.slotlink.screen.LinkScreenHandler
import badasintended.slotlink.util.c2s
import badasintended.slotlink.util.getClient
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack

@Environment(EnvType.CLIENT)
class FilterSlotWidget(
    private val handler: LinkScreenHandler,
    private val index: Int,
    x: Int, y: Int
) : SlotWidget(x, y, 18, handler.playerInv, { handler.filter[index].first }) {

    private val nbt get() = handler.filter[index].second

    override fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        super.renderOverlay(matrices, stack)

        getClient().apply {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 1, y + 1, "")

            matrices.push()
            matrices.translate(0.0, 0.0, itemRenderer.zOffset + 200.0)
            fill(matrices, x + 1, y + 1, x + 17, y + 17, if (nbt) 0x70aa27ba else 0x408b8b8b)
            if (nbt) {
                textRenderer.drawWithShadow(matrices, "+", x + 17f - textRenderer.getWidth("+"), y + 17f - textRenderer.fontHeight, 0xaa27ba)
            }
            matrices.pop()
        }
    }

    override fun onClick(button: Int) {
        handler.filterSlotClick(index, button)
        c2s(Packets.FILTER_SLOT_CLICK) {
            writeVarInt(handler.syncId)
            writeVarInt(index)
            writeVarInt(button)
        }
    }

}
