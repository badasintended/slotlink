package badasintended.slotlink.mixin;

import badasintended.slotlink.compat.trinkets.RemoteTrinket;
import badasintended.slotlink.item.RemoteItem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements RemoteItem.Holder, RemoteTrinket.Holder {

    @Unique
    private final IntSet possibleRemoteSlots = new IntOpenHashSet();

    @Unique
    @Nullable
    private Object remoteTrinketSlot = null;

    @NotNull
    @Override
    public IntSet slotlink$getPossibleRemoteSlots() {
        return possibleRemoteSlots;
    }

    @Nullable
    @Override
    public Object slotlink$getRemoteTrinketSlot() {
        return remoteTrinketSlot;
    }

    @Override
    public void slotlink$setRemoteTrinketSlot(@Nullable Object o) {
        remoteTrinketSlot = o;
    }

}
