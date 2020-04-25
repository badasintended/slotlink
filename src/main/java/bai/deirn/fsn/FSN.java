package bai.deirn.fsn;

import bai.deirn.fsn.config.ModConfig;
import bai.deirn.fsn.registry.BlockEntityTypeRegistry;
import bai.deirn.fsn.registry.BlockRegistry;
import bai.deirn.fsn.registry.ItemRegistry;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class FSN implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        BlockRegistry.init();
        BlockEntityTypeRegistry.init();
        ItemRegistry.init();
    }

}
