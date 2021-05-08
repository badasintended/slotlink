package badasintended.slotlink.mixin;

import badasintended.slotlink.inventory.PairInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DoubleInventory.class)
public abstract class DoubleInventoryMixin implements PairInventory {

    @Shadow
    @Final
    private Inventory first;

    @Shadow
    @Final
    private Inventory second;

    @Shadow
    public abstract boolean isPart(Inventory inventory);

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
        return isPart(inventory);
    }

}
