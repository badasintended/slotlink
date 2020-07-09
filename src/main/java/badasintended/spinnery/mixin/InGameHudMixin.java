/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.mixin;

import badasintended.spinnery.client.screen.InGameHudScreen;
import badasintended.spinnery.widget.WInterface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injections into InGameHudScreen to
 * allow for addition of Spinner widgets.
 */
@Mixin(InGameHud.class)
public class InGameHudMixin implements InGameHudScreen.Accessor {
	WInterface cursed_hudInterface = new WInterface();

	@Inject(method = "<init>", at = @At("RETURN"))
	public void onInitialize(MinecraftClient client, CallbackInfo ci) {
		InGameHudScreen.onInitialize(cursed_getInGameHud());
	}

	@Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("RETURN"))
	public void renderInterfaces(MatrixStack matrices, float f, CallbackInfo callbackInformation) {
		VertexConsumerProvider.Immediate provider = VertexConsumerProvider.immediate(MinecraftClient.getInstance().getBufferBuilders().getBlockBufferBuilders().get(RenderLayer.getSolid()));

		cursed_hudInterface.draw(matrices, provider);

		provider.draw();
	}

	@Override
	public WInterface cursed_getInterface() {
		return cursed_hudInterface;
	}

	@Override
	public InGameHud cursed_getInGameHud() {
		return (InGameHud) (Object) this;
	}
}
