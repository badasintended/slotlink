package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import sbinnery.client.render.BaseRenderer
import sbinnery.widget.WSlot
import sbinnery.widget.api.Color
import kotlin.math.floor

@Environment(EnvType.CLIENT)
class WFilterSlot(
    private val update: (ItemStack) -> Any
) : WSlot() {

    private var filterStack: ItemStack = ItemStack.EMPTY

    override fun getStack() = filterStack

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        val x = floor(x)
        val y = floor(y)

        super.draw(matrices, provider)
        BaseRenderer.drawQuad(
            matrices, provider, x, y, z + 201, 18f, 18f,
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

    @Suppress("UNCHECKED_CAST")
    override fun <W : WSlot> setStack(stack: ItemStack): W {
        this.filterStack = stack
        return this as W
    }

    override fun onMouseClicked(mouseX: Float, mouseY: Float, button: Int) {}
    override fun onMouseDragged(mouseX: Float, mouseY: Float, button: Int, deltaX: Double, deltaY: Double) {}

}
