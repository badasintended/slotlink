/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.client.render.BaseRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class WTextField extends WAbstractTextEditor {
	protected Integer fixedLength;

	public Integer getFixedLength() {
		return fixedLength;
	}

	@SuppressWarnings("unchecked")
	public <W extends WTextField> W setFixedLength(Integer fixedLength) {
		this.fixedLength = fixedLength;
		return (W) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <W extends WAbstractTextEditor> W setText(String text) {
		String finalText = text.replaceAll("\n", "");
		if (fixedLength != null && fixedLength >= 0 && fixedLength < finalText.length()) {
			finalText = finalText.substring(0, fixedLength);
		}
		return (W) super.setText(finalText);
	}

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

		BaseRenderer.drawBeveledPanel(matrices, provider, x, y, z, sX, sY, getStyle().asColor("top_left"), getStyle().asColor("background"), getStyle().asColor("bottom_right"));

		renderField(matrices, provider);
	}
}
