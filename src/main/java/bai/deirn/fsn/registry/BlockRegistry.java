package bai.deirn.fsn.registry;

import bai.deirn.fsn.Utils;
import bai.deirn.fsn.block.*;
import net.minecraft.block.Block;

public abstract class BlockRegistry {

    public static final Block MASTER = new MasterBlock(ModBlock.SETTINGS);
    public static final Block CRAFTING_TERMINAL = new CraftingTerminalBlock(ModBlock.SETTINGS);
    public static final Block PROCESSING_TERMINAL = new ProcessingTerminalBlock(ModBlock.SETTINGS);

    public static final Block CABLE = new CableBlock(CableBlock.SETTINGS);
    public static final Block STORAGE_CABLE = new StorageCableBlock(CableBlock.SETTINGS);
    public static final Block IMPORT_CABLE = new ImportCableBlock(CableBlock.SETTINGS);
    public static final Block EXPORT_CABLE = new ExportCableBlock(CableBlock.SETTINGS);
    public static final Block PROCESSING_CABLE = new ProcessingCableBlock(CableBlock.SETTINGS);

    public static void init() {
        Utils.register("master", MASTER);
        Utils.register("crafting_terminal", CRAFTING_TERMINAL);
        Utils.register("processing_terminal", PROCESSING_TERMINAL);
        Utils.register("cable", CABLE);
        Utils.register("storage_cable", STORAGE_CABLE);
        Utils.register("import_cable", IMPORT_CABLE);
        Utils.register("export_cable", EXPORT_CABLE);
        Utils.register("processing_cable", PROCESSING_CABLE);
    }

}
