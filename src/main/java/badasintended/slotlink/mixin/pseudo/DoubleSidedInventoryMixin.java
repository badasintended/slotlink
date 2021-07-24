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
@Mixin(targets = "ninjaphenix.expandedstorage.common.inventory.DoubleSidedInventory")
public abstract class DoubleSidedInventoryMixin implements PairInventory {

    @Shadow
    @Final
    SidedInventory first;

    @Shadow
    @Final
    SidedInventory second;

    @Shadow
    abstract boolean isPart(final SidedInventory inventory);

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
        return inventory instanceof SidedInventory && isPart((SidedInventory) inventory);
    }

}
