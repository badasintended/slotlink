package io.gitlab.intended.storagenetworks.block.entity;

import io.gitlab.intended.storagenetworks.registry.BlockEntityTypeRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class MasterBlockEntity extends BlockEntity {

    private ListTag storagePos = new ListTag();
    private List<int[]> storagePosArray = new ArrayList<>();

    public MasterBlockEntity() {
        super(BlockEntityTypeRegistry.MASTER);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        storagePosArray.forEach(storage -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("x", storage[0]);
            compoundTag.putInt("y", storage[1]);
            compoundTag.putInt("z", storage[2]);
            storagePos.add(compoundTag);
        });

        tag.put("storagePos", storagePos);

        return tag;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);

        storagePos = (ListTag) tag.get("storagePos");
        storagePosArray.clear();
        if (storagePos != null) {
            for (Tag storage : storagePos) {
                CompoundTag compoundTag = (CompoundTag) storage;
                int[] ints = {
                        compoundTag.getInt("x"),
                        compoundTag.getInt("y"),
                        compoundTag.getInt("z")
                };
                storagePosArray.add(ints);
            }
        }
    }

}
