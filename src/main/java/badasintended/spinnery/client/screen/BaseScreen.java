/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.client.screen;

import badasintended.spinnery.common.utility.MouseUtilities;
import badasintended.spinnery.widget.WInterface;
import badasintended.spinnery.widget.api.WInterfaceProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class BaseScreen extends Screen implements WInterfaceProvider {
	protected final WInterface screenInterface = new WInterface();

	private boolean isPauseScreen = false;

	public BaseScreen() {
		super(new LiteralText(""));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float tick) {
		VertexConsumerProvider.Immediate provider = VertexConsumerProvider.immediate(MinecraftClient.getInstance().getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getSolid()));

		getInterface().draw(matrices, provider);

		provider.draw();
	}

	@Override
	public WInterface getInterface() {
		return screenInterface;
	}

	@Override
	public boolean keyPressed(int keyCode, int character, int keyModifier) {
		screenInterface.onKeyPressed(keyCode, character, keyModifier);

		if (keyCode == GLFW.GLFW_KEY_ESCAPE && shouldCloseOnEsc()) {
			onClose();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tick() {
		getInterface().tick();
	}

	@Override
	public boolean isPauseScreen() {
		return isPauseScreen;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		screenInterface.onAlign();
		super.resize(client, width, height);
	}

	public <S extends BaseScreen> S setIsPauseScreen(boolean isPauseScreen) {
		this.isPauseScreen = isPauseScreen;
		return (S) this;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		screenInterface.onMouseClicked((float) mouseX, (float) mouseY, mouseButton);
		return false;
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		screenInterface.onMouseReleased((float) mouseX, (float) mouseY, mouseButton);
		return false;
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		screenInterface.onMouseDragged((float) mouseX, (float) mouseY, mouseButton, deltaX, deltaY);
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double deltaY) {
		screenInterface.onMouseScrolled((float) mouseX, (float) mouseY, deltaY);
		return false;
	}

	@Override
	public boolean keyReleased(int character, int keyCode, int keyModifier) {
		screenInterface.onKeyReleased(character, keyCode, keyModifier);
		return false;
	}

	@Override
	public boolean charTyped(char character, int keyCode) {
		screenInterface.onCharTyped(character, keyCode);
		return super.charTyped(character, keyCode);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		screenInterface.onMouseMoved((float) mouseX, (float) mouseY);

		MouseUtilities.mouseX = (float) mouseX;
		MouseUtilities.mouseY = (float) mouseY;
	}
}
