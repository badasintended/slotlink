package deirn.minecraft.fsn.block;

public class FSNBlocks {

    public static final FSNBlock CONTROLLER = new FSNBlock("controller");
    public static final FSNBlock CRAFTING_TERMINAL = new FSNBlock("crafting_terminal");
    public static final FSNBlock PROCESSING_TERMINAL = new FSNBlock("processing_terminal");

    public static final FSNBlock CABLE = new Cable("cable");
    public static final FSNBlock STORAGE_CABLE = new ConnectorCable("storage_cable");
    public static final FSNBlock IMPORT_CABLE = new ConnectorCable("import_cable");
    public static final FSNBlock EXPORT_CABLE = new ConnectorCable("export_cable");
    public static final FSNBlock PROCESSING_CABLE = new ConnectorCable("processing_cable");

    public static void init() {
        BlockUtils.register(
                CONTROLLER,
                CRAFTING_TERMINAL,
                PROCESSING_TERMINAL,
                CABLE,
                STORAGE_CABLE,
                IMPORT_CABLE,
                EXPORT_CABLE,
                PROCESSING_CABLE
        );
    }

}
