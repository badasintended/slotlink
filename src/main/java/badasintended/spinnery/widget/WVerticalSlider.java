/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.client.render.BaseRenderer;
import badasintended.spinnery.client.render.TextRenderer;
import badasintended.spinnery.widget.api.Position;
import badasintended.spinnery.widget.api.Size;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WVerticalSlider extends WAbstractSlider {
	@Override
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
		if (isHidden()) {
			return;
		}

		float x = getX();
		float y = getY();
		float z = getZ();

		float sX = getWidth();
		float sY = getHeight();

		if (isProgressVisible()) {
			Position tPos = getProgressTextAnchor();
			TextRenderer.pass().shadow(isLabelShadowed()).text(getFormattedProgress()).at(tPos.getX(), tPos.getY(), tPos.getZ())
					.color(getStyle().asColor("label.color")).shadowColor(getStyle().asColor("label.shadow_color")).render(matrices, provider);
		}

		BaseRenderer.drawQuad(matrices, provider, x, y, z, sX, 1, getStyle().asColor("top_left.background"));
		BaseRenderer.drawQuad(matrices, provider, x, y, z, 1, (sY), getStyle().asColor("top_left.background"));

		BaseRenderer.drawQuad(matrices, provider, x, y + (sY) - 1, z, sX, 1, getStyle().asColor("bottom_right.background"));
		BaseRenderer.drawQuad(matrices, provider, x + sX, y, z, 1, sY, getStyle().asColor("bottom_right.background"));

		Position innerAnchor = getInnerAnchor();
		Size innerSize = getInnerSize();
		float innerX = innerAnchor.getX();
		float innerY = innerAnchor.getY();
		float innerWidth = innerSize.getWidth();
		float innerHeight = innerSize.getHeight();
		float percentComplete = getPercentComplete();
		float percentLeft = 1 - percentComplete;
		BaseRenderer.drawQuad(matrices, provider, innerX, innerY + innerHeight * percentLeft, z, innerWidth, innerHeight * percentComplete,
				getStyle().asColor("background.on"));
		BaseRenderer.drawQuad(matrices, provider, innerX, innerY, z, innerWidth, innerHeight * percentLeft,
				getStyle().asColor("background.off"));

		Size knobSize = getKnobSize();
		float knobWidth = knobSize.getWidth();
		float knobHeight = knobSize.getHeight();
		float knobY = (y + (innerHeight - knobSize.getHeight() / 2f) * percentLeft);
		float clampedY = Math.min(y + innerHeight - knobHeight / 2f, Math.max(y, knobY));
		BaseRenderer.drawBeveledPanel(matrices, provider, x - 1, clampedY, z, knobWidth, knobHeight,
				getStyle().asColor("top_left.foreground"), getStyle().asColor("foreground"),
				getStyle().asColor("bottom_right.foreground"));
	}

	@Override
	public Position getProgressTextAnchor() {
		return Position.of(this).add(getWidth() + 4, getHeight() / 2 - TextRenderer.height() / 2, 0);
	}

	@Override
	public Size getKnobSize() {
		return Size.of(getWidth() + 3, 6);
	}

	@Override
	protected void updatePosition(float mouseX, float mouseY) {
		float innerHeight = getInnerSize().getHeight();
		float percentComplete = Math.max(0, (getInnerAnchor().getY() + innerHeight - mouseY) / innerHeight);
		setProgress(min + percentComplete * (max - min));
	}
}
