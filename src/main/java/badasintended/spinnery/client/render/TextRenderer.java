/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.client.render;

import badasintended.spinnery.widget.api.Color;
import badasintended.spinnery.widget.api.Position;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;

public class TextRenderer {
	public static RenderPass pass() {
		return new RenderPass();
	}

	public static int height() {
		return height(Font.DEFAULT);
	}

	public static int height(Font font) {
		return getTextRenderer(font).fontHeight;
	}

	public static net.minecraft.client.font.TextRenderer getTextRenderer(Font font) {
		return MinecraftClient.getInstance().textRenderer;
	}

	public static int width(char character) {
		return width(character, Font.DEFAULT);
	}

	public static int width(char character, Font font) {
		return getTextRenderer(font).getWidth(String.valueOf(character));
	}

	public static int width(String string) {
		return width(string, Font.DEFAULT);
	}

	public static int width(String string, Font font) {
		return getTextRenderer(font).getWidth(string);
	}

	public static int width(Text text, Font font) {
		return width(text.getString(), font);
	}

	public static int width(Text text) {
		return width(text.getString(), Font.DEFAULT);
	}

	public enum Font {
		DEFAULT,
		ENCHANTMENT,
	}

	public static class RenderPass {
		private String text;
		private String shadowText;
		private int x;
		private int y;
		private int z;
		private int color = 0xffffffff;
		private int shadowColor = 0xff3e3e3e;
		private double scale = 1.0;
		private boolean shadow;
		private Integer maxWidth;
		private Font font = Font.DEFAULT;

		public RenderPass text(String text) {
			this.text = text;
			this.shadowText = text.replaceAll("§[0-9a-f]", "");
			return this;
		}

		public RenderPass text(char c) {
			this.text = String.valueOf(c);
			this.shadowText = text;
			return this;
		}

		public RenderPass text(Text text) {
			this.text = text.getString();
			this.shadowText = this.text.replaceAll("§[0-9a-f]", "");
			return this;
		}

		public RenderPass at(Position position) {
			return at(position.getX(), position.getY(), position.getZ());
		}

		public RenderPass at(Number x, Number y, Number z) {
			this.x = x.intValue();
			this.y = y.intValue();
			this.z = z.intValue();
			return this;
		}

		public RenderPass size(int size) {
			return scale(size / 9D);
		}

		public RenderPass scale(double scale) {
			this.scale = scale;
			return this;
		}

		public RenderPass color(Color color) {
			return color(color.ARGB);
		}

		public RenderPass color(int color) {
			this.color = color;
			return this;
		}

		public RenderPass shadow(boolean shadow) {
			this.shadow = shadow;
			return this;
		}

		public RenderPass shadowColor(Color color) {
			return shadowColor(color.ARGB);
		}

		public RenderPass shadowColor(int color) {
			this.shadowColor = color;
			return this;
		}

		public RenderPass maxWidth(Integer maxWidth) {
			this.maxWidth = maxWidth;
			return this;
		}

		public RenderPass font(Font font) {
			this.font = font;
			return this;
		}

		public void render(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
			matrices.push();

			if (maxWidth != null) {
				if (shadow)
					getTextRenderer(font).drawTrimmed(StringRenderable.plain(shadowText), x + 1, y + 1, maxWidth, shadowColor);
				getTextRenderer(font).drawTrimmed(StringRenderable.plain(text), x, y, maxWidth, color);
			} else {
				if (shadow)
					getTextRenderer(font).draw(matrices, StringRenderable.plain(shadowText), x + 1, y + 1, shadowColor);
				getTextRenderer(font).draw(matrices, StringRenderable.plain(text), x, y, color);
			}

			matrices.pop();

			provider.draw();
		}
	}
}
