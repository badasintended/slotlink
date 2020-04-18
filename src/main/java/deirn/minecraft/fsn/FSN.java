package deirn.minecraft.fsn;

import deirn.minecraft.fsn.block.FSNBlocks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.util.logging.Logger;

public class FSN implements ModInitializer {

    public static final String MOD_ID = "fsn";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        FSNBlocks.init();
    }

}
