package deirn.minecraft.fsn.block;

public class FSNBlocks {

    public static final FSNBlock CONTROLLER = new FSNBlock("controller");
    public static final FSNBlock CRAFTING_TERMINAL = new FSNBlock("crafting_terminal");
    public static final FSNBlock PROCESSING_TERMINAL = new FSNBlock("processing_terminal");

    public static final FSNBlock CABLE = new CableBase("cable");

    public static void init() {
        BlockUtils.register(CONTROLLER, CRAFTING_TERMINAL, PROCESSING_TERMINAL, CABLE);
    }

}
