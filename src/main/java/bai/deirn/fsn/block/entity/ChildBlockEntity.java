package bai.deirn.fsn.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class ChildBlockEntity extends BlockEntity {

    protected boolean hasMaster = false;
    protected CompoundTag masterPos = new CompoundTag();
    protected int[] masterPosArray = new int[3];

    public ChildBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putBoolean("hasMaster", hasMaster);
        this.masterPos.putInt("x", masterPosArray[0]);
        this.masterPos.putInt("y", masterPosArray[1]);
        this.masterPos.putInt("z", masterPosArray[2]);
        tag.put("masterPos", this.masterPos);

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        this.hasMaster = tag.getBoolean("hasMaster");
        this.masterPos = tag.getCompound("masterPos");
        this.masterPosArray[0] = this.masterPos.getInt("x");
        this.masterPosArray[1] = this.masterPos.getInt("y");
        this.masterPosArray[2] = this.masterPos.getInt("z");
    }


}
