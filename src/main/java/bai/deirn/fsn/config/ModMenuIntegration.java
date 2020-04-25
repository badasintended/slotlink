package bai.deirn.fsn.config;

import bai.deirn.fsn.Utils;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;
import java.util.function.Supplier;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public String getModId() {
        return Utils.MOD_ID;
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen) {
        return Optional.of(AutoConfig.getConfigScreen(ModConfig.class, screen));
    }

}
