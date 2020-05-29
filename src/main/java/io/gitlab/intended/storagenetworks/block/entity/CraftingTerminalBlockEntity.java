package io.gitlab.intended.storagenetworks.block.entity;

import io.gitlab.intended.storagenetworks.registry.BlockEntityTypeRegistry;

public class CraftingTerminalBlockEntity extends ChildBlockEntity {

    public CraftingTerminalBlockEntity() {
        super(BlockEntityTypeRegistry.CRAFTING_TERMINAL);
    }

}
