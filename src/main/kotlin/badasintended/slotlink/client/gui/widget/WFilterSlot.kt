package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.item.ItemRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import spinnery.client.render.BaseRenderer
import spinnery.widget.WSlot
import spinnery.widget.api.Color

@Environment(EnvType.CLIENT)
class WFilterSlot(
    private val update: (ItemStack) -> Any
) : WVanillaSlot() {

    private var filterStack: ItemStack = ItemStack.EMPTY

    override fun getStack() = filterStack

    override fun drawItem(
        matrices: MatrixStack,
        provider: VertexConsumerProvider,
        stack: ItemStack,
        itemRenderer: ItemRenderer,
        textRenderer: TextRenderer,
        itemX: Int,
        itemY: Int
    ) {
        itemRenderer.renderGuiItemIcon(stack, itemX, itemY)
        BaseRenderer.drawQuad(
            matrices, provider, itemX.toFloat(), itemY.toFloat(), z + 201, 16f, 16f,
            Color.of(style.asColor("background.unfocused").RGB + 0x40000000)
        )
    }

    override fun onMouseReleased(mouseX: Float, mouseY: Float, button: Int) {
        if (!isFocused or isHidden) return

        filterStack = `interface`.handler.playerInventory.cursorStack.copy()
        filterStack.count = 1
        filterStack.tag = CompoundTag()
        update.invoke(stack)
    }

    override fun <W : WSlot> setStack(stack: ItemStack): W {
        this.filterStack = stack
        return this as W
    }

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {}
    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

}
