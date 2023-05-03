package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.client.util.client
import badasintended.slotlink.client.util.wrap
import badasintended.slotlink.init.Packets.MULTI_SLOT_ACTION
import badasintended.slotlink.screen.RequestScreenHandler
import badasintended.slotlink.util.enum
import badasintended.slotlink.util.int
import badasintended.slotlink.util.toFormattedString
import kotlin.math.ceil
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType.CLONE
import net.minecraft.screen.slot.SlotActionType.PICKUP
import net.minecraft.screen.slot.SlotActionType.QUICK_MOVE
import net.minecraft.screen.slot.SlotActionType.SWAP
import net.minecraft.screen.slot.SlotActionType.THROW
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

/**
 * [MultiSlotWidget] is an impostor.
 */
@Environment(EnvType.CLIENT)
class MultiSlotWidget(
    handler: RequestScreenHandler,
    private val index: Int,
    x: Int, y: Int
) : SlotWidget<RequestScreenHandler>(x, y, 18, handler, { handler.itemViews[index].singleStack }),
    KeyGrabber {

    private val count get() = handler.itemViews[index].count

    override fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        client.apply {
            itemRenderer.renderGuiItemOverlay(matrices, textRenderer, stack, x + 1, y + 1, "")

            val factor = window.scaleFactor.toFloat()
            val scale = (1 / factor) * ceil(factor / 2)

            val countText = if (count <= 1) "" else count.toFormattedString()

            matrices.wrap {
                matrices.translate(0.0, 0.0, 250.0)
                matrices.scale(scale, scale, 1f)
                textRenderer.drawWithShadow(
                    matrices, countText, ((x + 17 - (textRenderer.getWidth(countText) * scale)) / scale),
                    ((y + 17 - (textRenderer.fontHeight * scale)) / scale), 0xFFFFFF
                )
            }
        }
    }

    override fun appendTooltip(tooltip: MutableList<Text>) {
        (tooltip[0] as MutableText).append(Text.literal(" (${count})").formatted(Formatting.GOLD))
    }

    override fun onClick(button: Int) {
        c2s(MULTI_SLOT_ACTION) {
            int(handler.syncId)
            int(index)
            int(button)
            enum(if (button == 2) CLONE else if (Screen.hasShiftDown()) QUICK_MOVE else PICKUP)
        }
    }

    override fun onKey(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (hovered) {
            if (client.options.dropKey.matchesKey(keyCode, scanCode)) {
                c2s(MULTI_SLOT_ACTION) {
                    int(handler.syncId)
                    int(index)
                    int(if (!Screen.hasControlDown()) 0 else 1)
                    enum(THROW)
                }
                return true
            }
            val hotbar = client.options.hotbarKeys.indexOfFirst { it.matchesKey(keyCode, scanCode) }
            if (hotbar >= 0) {
                c2s(MULTI_SLOT_ACTION) {
                    int(handler.syncId)
                    int(index)
                    int(hotbar)
                    enum(SWAP)
                }
            }
        }
        return false
    }

}
