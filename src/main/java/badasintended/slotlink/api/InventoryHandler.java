package badasintended.slotlink.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Custom inventory handling for weird inventories.
 * only called on request block and remote.
 */
public abstract class InventoryHandler<T extends BlockEntity> {

    public final Inventory getHandler(@NotNull T t) {
        return new Inventory() {

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return InventoryHandler.this.isValid(t, slot, stack);
            }

            @Override
            public void clear() {
            }

            @Override
            public int size() {
                return InventoryHandler.this.size(t);
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack getStack(int slot) {
                return InventoryHandler.this.getStack(t, slot);
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeStack(int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {
                InventoryHandler.this.setStack(t, slot, stack);
            }

            @Override
            public void markDirty() {
                t.markDirty();
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return true;
            }

        };
    }

    public abstract int size(@NotNull T t);

    public abstract boolean isValid(@NotNull T t, int slot, @NotNull ItemStack stack);

    @NotNull
    public abstract ItemStack getStack(@NotNull T t, int slot);

    public abstract void setStack(@NotNull T t, int slot, @NotNull ItemStack stack);

}
