package badasintended.slotlink.client.gui.screen

import badasintended.slotlink.gui.screen.ModScreenHandler
import me.shedaniel.rei.impl.ScreenHelper
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import spinnery.client.render.BaseRenderer
import spinnery.client.render.layer.SpinneryLayers
import spinnery.client.screen.BaseHandledScreen
import spinnery.widget.WInterface

@Environment(EnvType.CLIENT)
abstract class ModScreen<C : ModScreenHandler>(
    protected val c: C
) : BaseHandledScreen<C>(LiteralText(""), c, c.player) {

    protected val root: WInterface = `interface`

    /**
     * Explicitly use vanilla item renderer to prevent incompatibility with other mod.
     *
     * TODO: remove when fixed in spinnery
     */
    override fun render(matrices: MatrixStack, mouseX: Int, mouseY: Int, tickDelta: Float) {
        renderBackground(matrices)

        val provider = MinecraftClient.getInstance().bufferBuilders.entityVertexConsumers

        root.draw(matrices, provider)

        root.allWidgets.forEach { widget ->
            if (widget.isFocused) renderTooltip(matrices, widget.tooltip, mouseX, mouseY)
        }

        provider.draw(SpinneryLayers.getFlat())
        provider.draw(SpinneryLayers.getTooltip())
        provider.draw()

        val stack = if (
            c.previewCursorStack.isEmpty
            and c.getDragSlots(0).isNullOrEmpty()
            and c.getDragSlots(1).isNullOrEmpty()
        )
            c.playerInventory.cursorStack else c.previewCursorStack

        val itemRenderer = BaseRenderer.getDefaultItemRenderer()

        itemRenderer.zOffset += 200
        itemRenderer.renderInGui(stack, mouseX - 8, mouseY - 8)
        itemRenderer.renderGuiItemOverlay(BaseRenderer.getDefaultTextRenderer(), stack, mouseX - 8, mouseY - 8)
        itemRenderer.zOffset -= 200

        if (drawSlot != null) if (stack.isEmpty and !drawSlot.stack.isEmpty) {
            renderTooltip(matrices, drawSlot.stack, mouseX, mouseY)
        }

        // well this is cursed
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            ScreenHelper.getLastOverlay().render(matrices, mouseX, mouseY, tickDelta)
        }
    }

}
