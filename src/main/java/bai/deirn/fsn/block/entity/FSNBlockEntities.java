package bai.deirn.fsn.block.entity;

import bai.deirn.fsn.block.FSNBlocks;
import bai.deirn.fsn.util.Utils;
import net.minecraft.block.entity.BlockEntityType;

public class FSNBlockEntities {

    public static final BlockEntityType<?> CRAFTING_TERMINAL = Utils.createBlockEntity(CraftingTerminalBlockEntity::new, FSNBlocks.CRAFTING_TERMINAL);
    public static final BlockEntityType<?> PROCESSING_TERMINAL = Utils.createBlockEntity(ProcessingTerminalBlockEntity::new, FSNBlocks.PROCESSING_TERMINAL);

    public static final BlockEntityType<?> CABLE = Utils.createBlockEntity(CableBlockEntity::new, FSNBlocks.CABLE);

    public static void init(){
        Utils.register("crafting_terminal", CRAFTING_TERMINAL);
        Utils.register("processing_terminal", PROCESSING_TERMINAL);
        Utils.register("cable", CABLE);
    }

}
