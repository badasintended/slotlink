package io.gitlab.intended.storagenetworks.block.entity;

import io.gitlab.intended.storagenetworks.registry.BlockEntityTypeRegistry;

public class CableBlockEntity extends ChildBlockEntity {

    public CableBlockEntity() {
        super(BlockEntityTypeRegistry.CABLE);
    }

}
