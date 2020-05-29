package io.gitlab.intended.storagenetworks.block.entity;

import io.gitlab.intended.storagenetworks.registry.BlockEntityTypeRegistry;

public class ProcessingTerminalBlockEntity extends ChildBlockEntity {

    public ProcessingTerminalBlockEntity() {
        super(BlockEntityTypeRegistry.PROCESSING_TERMINAL);
    }

}
