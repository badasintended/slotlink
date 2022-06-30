package badasintended.slotlink.client.gui.widget

import badasintended.slotlink.client.compat.rei.ReiAccess
import badasintended.slotlink.client.util.c2s
import badasintended.slotlink.client.util.client
import badasintended.slotlink.client.util.wrap
import badasintended.slotlink.init.Packets
import badasintended.slotlink.screen.FilterScreenHandler
import badasintended.slotlink.util.bool
import badasintended.slotlink.util.int
import badasintended.slotlink.util.stack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

@Environment(EnvType.CLIENT)
class FilterSlotWidget(
    handler: FilterScreenHandler,
    val index: Int,
    x: Int, y: Int
) : SlotWidget<FilterScreenHandler>(x, y, 18, handler, { handler.filter[index].first }) {

    private val nbt get() = handler.filter[index].second

    override fun appendTooltip(tooltip: MutableList<Text>) {
        tooltip.add(1, Text.translatable("container.slotlink.filter.slot.nbt.${nbt}").formatted(Formatting.GRAY))
    }

    override fun renderOverlay(matrices: MatrixStack, stack: ItemStack) {
        super.renderOverlay(matrices, stack)

        client.apply {
            itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + 1, y + 1, "")

            matrices.wrap {
                matrices.translate(0.0, 0.0, itemRenderer.zOffset + 200.0)
                fill(matrices, x + 1, y + 1, x + 17, y + 17, if (nbt) 0x70aa27ba else 0x408b8b8b)
                if (nbt) {
                    textRenderer.drawWithShadow(
                        matrices,
                        "+",
                        x + 17f - textRenderer.getWidth("+"),
                        y + 17f - textRenderer.fontHeight,
                        0xaa27ba
                    )
                }
            }
        }
    }

    override fun renderTooltip(matrices: MatrixStack, mouseX: Int, mouseY: Int) {
        super.renderTooltip(matrices, mouseX, mouseY)

        if (!handler.cursorStack.isEmpty || ReiAccess.isDraggingStack()) matrices.wrap {
            matrices.translate(0.0, 0.0, +256.0)
            val tlKey = "container.slotlink.filter.slot.tip." +
                if (Screen.hasControlDown()) "pressed" else
                    if (MinecraftClient.IS_SYSTEM_MAC) "cmd"
                    else "ctrl"
            client.currentScreen?.renderTooltip(matrices, Text.translatable(tlKey), mouseX, mouseY)
        }
    }

    override fun onClick(button: Int) {
        handler.filterSlotClick(index, handler.cursorStack, Screen.hasControlDown())
        c2s(Packets.FILTER_SLOT_CLICK) {
            int(handler.syncId)
            int(index)
            stack(handler.cursorStack)
            bool(Screen.hasControlDown())
        }
    }

}
