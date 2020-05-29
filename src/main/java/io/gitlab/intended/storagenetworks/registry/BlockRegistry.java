package io.gitlab.intended.storagenetworks.registry;

import io.gitlab.intended.storagenetworks.block.*;
import io.gitlab.intended.storagenetworks.item.ModItem;
import net.minecraft.item.BlockItem;
import net.minecraft.util.registry.Registry;

public final class BlockRegistry {

    public static final ModBlock MASTER = new MasterBlock("master");
    public static final ModBlock CRAFTING_TERMINAL = new CraftingTerminalBlock("crafting_terminal");
    public static final ModBlock PROCESSING_TERMINAL = new ProcessingTerminalBlock("processing_terminal");

    public static final ModBlock CABLE = new CableBlock("cable");
    public static final ModBlock STORAGE_CABLE = new StorageCableBlock("storage_cable");
    public static final ModBlock IMPORT_CABLE = new ImportCableBlock("import_cable");
    public static final ModBlock EXPORT_CABLE = new ExportCableBlock("export_cable");
    public static final ModBlock PROCESSING_CABLE = new ProcessingCableBlock("processing_cable");

    public static void register() {
        register(MASTER, CRAFTING_TERMINAL, PROCESSING_TERMINAL, CABLE, STORAGE_CABLE, IMPORT_CABLE, EXPORT_CABLE, PROCESSING_CABLE);
    }

    protected static void register(ModBlock... blocks) {
        for (ModBlock block : blocks) {
            Registry.register(Registry.BLOCK, block.ID, block);
            Registry.register(Registry.ITEM, block.ID, new BlockItem(block, ModItem.getSettings()));
        }
    }

}
