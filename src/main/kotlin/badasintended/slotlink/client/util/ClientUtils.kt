@file:Environment(EnvType.CLIENT)

package badasintended.slotlink.client.util

import badasintended.slotlink.util.buf
import badasintended.slotlink.util.modId
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.GameRenderer
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

fun Direction.texture(): Identifier {
    return modId("textures/gui/side_${asString()}.png")
}

val client: MinecraftClient
    get() = MinecraftClient.getInstance()

inline fun c2s(id: Identifier, buf: PacketByteBuf.() -> Unit) {
    ClientPlayNetworking.send(id, buf().apply(buf))
}

object GuiTextures {

    val REQUEST = modId("textures/gui/request.png")
    val CRAFTING = modId("textures/gui/crafting.png")
    val FILTER = modId("textures/gui/filter.png")

}

fun Identifier.bind() {
    RenderSystem.setShader(GameRenderer::getPositionTexShader)
    RenderSystem.setShaderTexture(0, this)
}
