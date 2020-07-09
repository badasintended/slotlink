/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.client.render;

import badasintended.spinnery.client.render.layer.SpinneryLayers;
import badasintended.spinnery.widget.api.Color;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BaseRenderer {
	public static void drawQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, Color color) {
		drawQuad(matrices, provider, x, y, z, sX, sY, 0x00f000f0, color);
	}

	public static void drawQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, int light, Color color) {
		matrices.push();

		VertexConsumer consumer = provider.getBuffer(SpinneryLayers.getInterface());

		consumer.vertex(matrices.peek().getModel(), x, y, z).color(color.R, color.G, color.B, color.A).light(light).next();
		consumer.vertex(matrices.peek().getModel(), x, y + sY, z).color(color.R, color.G, color.B, color.A).light(light).next();
		consumer.vertex(matrices.peek().getModel(), x + sX, y + sY, z).color(color.R, color.G, color.B, color.A).light(light).next();
		consumer.vertex(matrices.peek().getModel(), x + sX, y, z).color(color.R, color.G, color.B, color.A).light(light).next();

		matrices.pop();

		provider.draw();
	}

	public static void drawGradientQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float startX, float startY, float endX, float endY, float z, Color colorStart, Color colorEnd) {
		drawGradientQuad(matrices, provider, startX, startY, endX, endY, z, 0, 0, 1, 1, 0x00f000f0, colorStart, colorEnd, false);
	}

	public static void drawGradientQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float startX, float startY, float endX, float endY, float z, int light, Color colorStart, Color colorEnd) {
		drawGradientQuad(matrices, provider, startX, startY, endX, endY, z, 0, 0, 1, 1, light, colorStart, colorEnd, false);
	}

	public static void drawGradientQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float startX, float startY, float endX, float endY, float z, float uS, float vS, float uE, float vE, int light, Color colorStart, Color colorEnd, boolean textured) {
		if (!textured) RenderSystem.disableTexture();

		matrices.push();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		VertexConsumer consumer = provider.getBuffer(SpinneryLayers.getInterface());

		consumer.vertex(matrices.peek().getModel(), endX, startY, z + 201).color(colorStart.R, colorStart.G, colorStart.B, colorStart.A).texture(uS, vS).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
		consumer.vertex(matrices.peek().getModel(), startX, startY, z + 201).color(colorStart.R, colorStart.G, colorStart.B, colorStart.A).texture(uS, vE).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
		consumer.vertex(matrices.peek().getModel(), startX, endY, z + 201).color(colorEnd.R, colorEnd.G, colorEnd.B, colorEnd.A).texture(uE, vS).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();
		consumer.vertex(matrices.peek().getModel(), endX, endY, z + 201).color(colorEnd.R, colorEnd.G, colorEnd.B, colorEnd.A).texture(uE, vE).light(light).normal(matrices.peek().getNormal(), 0, 1, 0).next();

		RenderSystem.disableBlend();

		if (!textured) RenderSystem.enableTexture();

		matrices.pop();

		provider.draw();
	}

	public static void drawPanel(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, Color shadow, Color panel, Color hilight, Color outline) {
		drawQuad(matrices, provider, x + 3, y + 3, z, sX - 6, sY - 6, 0x00f000f0, panel);

		drawQuad(matrices, provider, x + 2, y + 1, z, sX - 4, 2, 0x00f000f0, hilight);
		drawQuad(matrices, provider, x + 2, y + sY - 3, z, sX - 4, 2, 0x00f000f0, shadow);
		drawQuad(matrices, provider, x + 1, y + 2, z, 2, sY - 4, 0x00f000f0, hilight);
		drawQuad(matrices, provider, x + sX - 3, y + 2, z, 2, sY - 4, 0x00f000f0, shadow);
		drawQuad(matrices, provider, x + sX - 3, y + 2, z, 1, 1, 0x00f000f0, panel);
		drawQuad(matrices, provider, x + 2, y + sY - 3, z, 1, 1, 0x00f000f0, panel);
		drawQuad(matrices, provider, x + 3, y + 3, z, 1, 1, 0x00f000f0, hilight);
		drawQuad(matrices, provider, x + sX - 4, y + sY - 4, z, 1, 1, 0x00f000f0, shadow);

		drawQuad(matrices, provider, x + 2, y, z, sX - 4, 1, 0x00f000f0, outline);
		drawQuad(matrices, provider, x, y + 2, z, 1, sY - 4, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + sX - 1, y + 2, z, 1, sY - 4, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + 2, y + sY - 1, z, sX - 4, 1, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + 1, y + 1, z, 1, 1, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + 1, y + sY - 2, z, 1, 1, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + sX - 2, y + 1, z, 1, 1, 0x00f000f0, outline);
		drawQuad(matrices, provider, x + sX - 2, y + sY - 2, z, 1, 1, 0x00f000f0, outline);
	}

	public static void drawBeveledPanel(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, Color topleft, Color panel, Color bottomright) {
		drawBeveledPanel(matrices, provider, x, y, z, sX, sY, 0x00f000f0, topleft, panel, bottomright);
	}

	public static void drawBeveledPanel(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, int light, Color topleft, Color panel, Color bottomright) {
		drawQuad(matrices, provider, x, y, z, sX, sY, light, panel);
		drawQuad(matrices, provider, x, y, z, sX, 1, light, topleft);
		drawQuad(matrices, provider, x, y + 1, z, 1, sY - 1, light, topleft);
		drawQuad(matrices, provider, x + sX - 1, y + 1, z, 1, sY - 1, light, bottomright);
		drawQuad(matrices, provider, x, y + sY - 1, z, sX - 1, 1, light, bottomright);
	}

	public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, Identifier texture) {
		drawTexturedQuad(matrices, provider, x, y, z, sX, sY, 0, 0, 1, 1, 0x00f000f0, Color.DEFAULT, texture);
	}

	public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, Color color, Identifier texture) {
		drawTexturedQuad(matrices, provider, x, y, z, sX, sY, 0, 0, 1, 1, 0x00f000f0, color, texture);
	}

	public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, int light, Color color, Identifier texture) {
		drawTexturedQuad(matrices, provider, x, y, z, sX, sY, 0, 0, 1, 1, light, color, texture);
	}

	public static void drawTexturedQuad(MatrixStack matrices, VertexConsumerProvider.Immediate provider, float x, float y, float z, float sX, float sY, float u0, float v0, float u1, float v1, int light, Color color, Identifier texture) {
		getTextureManager().bindTexture(texture);

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(770, 771, 1, 0);

		VertexConsumer consumer = provider.getBuffer(SpinneryLayers.get(texture));

		matrices.push();

		consumer.vertex(matrices.peek().getModel(), x, y + sY, z).color(color.R, color.G, color.B, color.A).texture(u0, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrices.peek().getNormal(), 0, 0, 0).next();
		consumer.vertex(matrices.peek().getModel(), x + sX, y + sY, z).color(color.R, color.G, color.B, color.A).texture(u1, v1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrices.peek().getNormal(), 0, 0, 0).next();
		consumer.vertex(matrices.peek().getModel(), x + sX, y, z).color(color.R, color.G, color.B, color.A).texture(u1, v0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrices.peek().getNormal(), 0, 0, 0).next();
		consumer.vertex(matrices.peek().getModel(), x, y, z).color(color.R, color.G, color.B, color.A).texture(u0, v0).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrices.peek().getNormal(), 0, 0, 0).next();

		RenderSystem.disableBlend();

		matrices.pop();

		provider.draw();
	}

	public static TextureManager getTextureManager() {
		return MinecraftClient.getInstance().getTextureManager();
	}

	public static ItemRenderer getItemRenderer() {
		return MinecraftClient.getInstance().getItemRenderer();
	}

	public static TextRenderer getTextRenderer() {
		return MinecraftClient.getInstance().textRenderer;
	}
}
