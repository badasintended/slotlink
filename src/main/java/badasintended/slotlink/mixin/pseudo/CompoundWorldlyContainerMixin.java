package badasintended.slotlink.mixin.pseudo;

import badasintended.slotlink.inventory.PairInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "ninjaphenix.expandedstorage.base.internal_api.inventory.CompoundWorldlyContainer")
public abstract class CompoundWorldlyContainerMixin implements PairInventory {

    @Shadow
    @Final
    SidedInventory first;

    @Shadow
    @Final
    SidedInventory second;

    @Shadow
    abstract boolean consistsPartlyOf(final SidedInventory inventory);

    @NotNull
    @Override
    public Inventory slotlink$getFirst() {
        return first;
    }

    @NotNull
    @Override
    public Inventory slotlink$getSecond() {
        return second;
    }

    @Override
    public boolean slotlink$isPart(@NotNull Inventory inventory) {
        return inventory instanceof SidedInventory && consistsPartlyOf((SidedInventory) inventory);
    }

}
