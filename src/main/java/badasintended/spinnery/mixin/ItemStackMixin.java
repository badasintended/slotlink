/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Shadow public abstract void setCount(int count);

	@Shadow private int count;

	@Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V")
	void onDeserialization(CompoundTag tag, CallbackInfo callbackInformation) {
		if (tag.contains("countInteger")) {
			setCount(tag.getInt("countInteger"));
		}
	}

	@Inject(at = @At("HEAD"), method = "toTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;")
	void onSerialization(CompoundTag tag, CallbackInfoReturnable<CompoundTag> callbackInformationReturnable) {
		if (this.count > Byte.MAX_VALUE) {
			tag.putInt("countInteger", this.count);
		}
	}
}
