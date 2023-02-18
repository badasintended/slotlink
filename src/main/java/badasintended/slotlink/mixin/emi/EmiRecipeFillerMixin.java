package badasintended.slotlink.mixin.emi;

import dev.emi.emi.EmiRecipeFiller;
import dev.emi.emi.mixin.accessor.ScreenHandlerAccessor;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// TODO: remove if new emi version is released
@Mixin(EmiRecipeFiller.class)
public abstract class EmiRecipeFillerMixin {

    // https://github.com/emilyploszaj/emi/pull/136
    @Redirect(require = 0, method = "getAllHandlers", at = @At(value = "INVOKE", target = "Ldev/emi/emi/mixin/accessor/ScreenHandlerAccessor;emi$getType()Lnet/minecraft/screen/ScreenHandlerType;"))
    private static ScreenHandlerType<?> slotlink(ScreenHandlerAccessor instance) {
        try {
            return instance instanceof PlayerScreenHandler ? null : ((ScreenHandler) instance).getType();
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

}
