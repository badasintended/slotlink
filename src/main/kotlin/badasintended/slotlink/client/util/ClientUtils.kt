@file:Environment(EnvType.CLIENT)

package badasintended.slotlink.client.util

import badasintended.slotlink.util.buf
import badasintended.slotlink.util.modId
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.util.math.MatrixStack
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

fun bindGuiTexture() {
    client.textureManager.bindTexture(guiTexture)
}

private typealias DH = DrawableHelper

fun drawNinePatch(matrices: MatrixStack, x: Int, y: Int, w: Int, h: Int, u: Float, v: Float, ltrb: Int, cm: Int) {
    drawNinePatch(matrices, x, y, w, h, u, v, ltrb, cm, ltrb)
}

fun drawNinePatch(
    matrices: MatrixStack,
    x: Int,
    y: Int,
    w: Int,
    h: Int,
    u: Float,
    v: Float,
    lt: Int,
    cm: Int,
    rb: Int
) {
    drawNinePatch(matrices, x, y, w, h, u, v, lt, cm, rb, lt, cm, rb)
}

/**
 * Wed Oct 14 01:43:12 PM UTC 2020
 * well i managed to write this shit
 *
 * @param x square x position
 * @param y square y position
 * @param w square width
 * @param h square height
 * @param u texture left position (in pixel)
 * @param v texture top position (in pixel)
 * @param l nine-patch left size
 * @param c nine-patch center size
 * @param r nine-patch right size
 * @param t nine-patch top size
 * @param m nine-patch middle size
 * @param b nine-patch bottom size
 */
fun drawNinePatch(
    matrices: MatrixStack,
    x: Int, y: Int, w: Int, h: Int,
    u: Float, v: Float,
    l: Int, c: Int, r: Int, t: Int, m: Int, b: Int
) {
    DH.drawTexture(matrices, x, y, l, t, u, v, l, t, 256, 256)
    DH.drawTexture(matrices, x + l, y, w - l - r, t, u + l, v, c, t, 256, 256)
    DH.drawTexture(matrices, x + w - r, y, r, t, u + l + c, v, r, t, 256, 256)

    DH.drawTexture(matrices, x, y + t, l, h - t - b, u, v + t, l, m, 256, 256)
    DH.drawTexture(matrices, x + l, y + t, w - l - r, h - t - b, u + l, v + t, c, m, 256, 256)
    DH.drawTexture(matrices, x + w - r, y + t, r, h - t - b, u + l + c, v + t, r, m, 256, 256)

    DH.drawTexture(matrices, x, y + h - b, l, b, u, v + t + m, l, b, 256, 256)
    DH.drawTexture(matrices, x + l, y + h - b, w - l - r, b, u + l, v + t + m, c, b, 256, 256)
    DH.drawTexture(matrices, x + w - r, y + h - b, r, b, u + l + c, v + t + m, r, b, 256, 256)
}

val guiTexture = modId("textures/gui/gui.png")