package badasintended.spinnery.mixin;

import badasintended.spinnery.client.screen.BaseContainerScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "render", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawForeground(Lnet/minecraft/client/util/math/MatrixStack;II)V"))
    void spinnery_render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (((HandledScreen) (Object) this) instanceof BaseContainerScreen) {
            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
            ci.cancel();
        }
    }

}
