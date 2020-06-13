package io.gitlab.intended.storagenetworks.gui.widget

import io.gitlab.intended.storagenetworks.block.BlockRegistry
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient
import net.minecraft.text.TranslatableText
import spinnery.client.render.BaseRenderer
import spinnery.client.render.TextRenderer
import spinnery.widget.WTextField

@Environment(EnvType.CLIENT)
class WInventorySearchBar : WTextField() {

    override fun draw() {
        if (isHidden) return
        BaseRenderer.drawBeveledPanel(
            x.toDouble(), y.toDouble(), z.toDouble(),
            width.toDouble(), height.toDouble(),
            style.asColor("top_left"),
            style.asColor("background.unfocused"),
            style.asColor("bottom_right")
        )

        renderField()
    }

    override fun renderField() {
        if (isEmpty && !active) {
            TextRenderer.pass()
                .text(TranslatableText(BlockRegistry.REQUEST.translationKey + ".search"))
                .at(innerAnchor.x, innerAnchor.y, z).scale(scale)
                .color(style.asColor("text.shadow_color"))
                .shadow(false)
            return
        }

        val glScale = MinecraftClient.getInstance().window.scaleFactor
        val rawHeight = MinecraftClient.getInstance().window.height

    }

}
