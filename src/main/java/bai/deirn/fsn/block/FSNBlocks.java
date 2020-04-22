package bai.deirn.fsn.block;

import bai.deirn.fsn.util.Utils;
import net.minecraft.block.Block;

public class FSNBlocks {

    public static final Block CONTROLLER = new FSNBlock(FSNBlock.SETTINGS);
    public static final Block CRAFTING_TERMINAL = new FSNBlock(FSNBlock.SETTINGS);
    public static final Block PROCESSING_TERMINAL = new FSNBlock(FSNBlock.SETTINGS);

    public static final Block CABLE = new CableBlock(CableBlock.SETTINGS);
    public static final Block STORAGE_CABLE = new ConnectorCableBlock(CableBlock.SETTINGS);
    public static final Block IMPORT_CABLE = new ConnectorCableBlock(CableBlock.SETTINGS);
    public static final Block EXPORT_CABLE = new ConnectorCableBlock(CableBlock.SETTINGS);
    public static final Block PROCESSING_CABLE = new ConnectorCableBlock(CableBlock.SETTINGS);

    public static void init() {
        Utils.register("controller", CONTROLLER);
        Utils.register("crafting_terminal", CRAFTING_TERMINAL);
        Utils.register("processing_terminal", PROCESSING_TERMINAL);
        Utils.register("cable", CABLE);
        Utils.register("storage_cable", STORAGE_CABLE);
        Utils.register("import_cable", IMPORT_CABLE);
        Utils.register("export_cable", EXPORT_CABLE);
        Utils.register("processing_cable", PROCESSING_CABLE);
    }

}
