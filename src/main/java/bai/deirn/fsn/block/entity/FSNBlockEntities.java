package bai.deirn.fsn.block.entity;

import bai.deirn.fsn.Utils;
import bai.deirn.fsn.block.FSNBlocks;
import net.minecraft.block.entity.BlockEntityType;

public abstract class FSNBlockEntities {

    public static final BlockEntityType<?> MASTER = Utils.createBlockEntity(MasterBlockEntity::new, FSNBlocks.MASTER);
    public static final BlockEntityType<?> CRAFTING_TERMINAL = Utils.createBlockEntity(CraftingTerminalBlockEntity::new, FSNBlocks.CRAFTING_TERMINAL);
    public static final BlockEntityType<?> PROCESSING_TERMINAL = Utils.createBlockEntity(ProcessingTerminalBlockEntity::new, FSNBlocks.PROCESSING_TERMINAL);

    public static final BlockEntityType<?> CABLE = Utils.createBlockEntity(CableBlockEntity::new, FSNBlocks.CABLE);
    public static final BlockEntityType<?> STORAGE_CABLE = Utils.createBlockEntity(StorageCableBlockEntity::new, FSNBlocks.STORAGE_CABLE);

    public static void init(){
        Utils.register("master", MASTER);
        Utils.register("crafting_terminal", CRAFTING_TERMINAL);
        Utils.register("processing_terminal", PROCESSING_TERMINAL);
        Utils.register("cable", CABLE);
        Utils.register("storage_cable", STORAGE_CABLE);
    }

}
