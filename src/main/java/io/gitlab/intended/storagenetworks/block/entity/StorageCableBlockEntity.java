package io.gitlab.intended.storagenetworks.block.entity;

import io.gitlab.intended.storagenetworks.registry.BlockEntityTypeRegistry;

public class StorageCableBlockEntity extends ChildBlockEntity {

    public StorageCableBlockEntity() {
        super(BlockEntityTypeRegistry.STORAGE_CABLE);
    }

}
