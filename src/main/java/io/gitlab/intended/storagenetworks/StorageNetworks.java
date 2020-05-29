package io.gitlab.intended.storagenetworks;

import io.gitlab.intended.storagenetworks.config.ModConfig;
import io.gitlab.intended.storagenetworks.registry.*;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageNetworks implements ModInitializer {

    public static final String MOD_ID = "storagenetworks";
    public static final String MOD_NAME = "Storage Networks";
    public static final Logger LOGGER = Logger.getLogger(MOD_ID);

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    public static TranslatableText translatable(String key) {
        return new TranslatableText(key);
    }

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        BlockEntityTypeRegistry.register();
        BlockRegistry.register();
        InventoryRegistry.register();
        ItemRegistry.register();
        TrinketRegistry.register();
    }

}