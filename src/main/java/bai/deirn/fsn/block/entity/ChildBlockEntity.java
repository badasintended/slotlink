package bai.deirn.fsn.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public class ChildBlockEntity extends BlockEntity {

    protected boolean hasController = false;
    protected int[] controllerPos = {4, 2, 0};

    public ChildBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putBoolean("hasController", this.hasController);
        tag.putIntArray("controllerPos", this.controllerPos);

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.hasController = tag.getBoolean("hasController");
        this.controllerPos = tag.getIntArray("controllerPos");
    }
}
