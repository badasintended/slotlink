package badasintended.slotlink.client.gui.widget

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

@Environment(EnvType.CLIENT)
class WDynamicLabel(private val string: () -> String) : WLabel() {

    override fun draw(matrices: MatrixStack, provider: VertexConsumerProvider.Immediate) {
        setText<WLabel>(string.invoke())
        super.draw(matrices, provider)
    }

}
