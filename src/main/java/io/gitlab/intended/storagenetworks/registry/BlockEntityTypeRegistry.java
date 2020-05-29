package io.gitlab.intended.storagenetworks.registry;

import io.gitlab.intended.storagenetworks.block.ModBlock;
import io.gitlab.intended.storagenetworks.block.entity.*;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class BlockEntityTypeRegistry {

    public static final BlockEntityType<MasterBlockEntity> MASTER = create(MasterBlockEntity::new, BlockRegistry.MASTER);
    public static final BlockEntityType<CraftingTerminalBlockEntity> CRAFTING_TERMINAL = create(CraftingTerminalBlockEntity::new, BlockRegistry.CRAFTING_TERMINAL);
    public static final BlockEntityType<ProcessingTerminalBlockEntity> PROCESSING_TERMINAL = create(ProcessingTerminalBlockEntity::new, BlockRegistry.PROCESSING_TERMINAL);

    public static final BlockEntityType<CableBlockEntity> CABLE = create(CableBlockEntity::new, BlockRegistry.CABLE);
    public static final BlockEntityType<StorageCableBlockEntity> STORAGE_CABLE = create(StorageCableBlockEntity::new, BlockRegistry.STORAGE_CABLE);

    public static void register() {
        register(BlockRegistry.MASTER, MASTER);
        register(BlockRegistry.CRAFTING_TERMINAL, CRAFTING_TERMINAL);
        register(BlockRegistry.PROCESSING_TERMINAL, PROCESSING_TERMINAL);
        register(BlockRegistry.CABLE, CABLE);
        register(BlockRegistry.STORAGE_CABLE, STORAGE_CABLE);
    }

    protected static void register(ModBlock block, BlockEntityType<? extends BlockEntity> blockEntityType) {
        Registry.register(Registry.BLOCK_ENTITY_TYPE, block.ID, blockEntityType);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(Supplier<T> supplier, Block block) {
        return BlockEntityType.Builder.create(supplier, block).build(null);
    }

}
