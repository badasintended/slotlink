package bai.deirn.fsn.block.entity;

import bai.deirn.fsn.registry.BlockEntityTypeRegistry;

public class CraftingTerminalBlockEntity extends ChildBlockEntity {

    public CraftingTerminalBlockEntity() {
        super(BlockEntityTypeRegistry.CRAFTING_TERMINAL);
    }

}
