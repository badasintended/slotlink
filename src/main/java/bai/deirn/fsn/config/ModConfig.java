package bai.deirn.fsn.config;

import bai.deirn.fsn.Utils;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = Utils.MOD_ID)
@Config.Gui.Background(Utils.MOD_ID+":textures/gui/config.png")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean remote0 = true;
    @ConfigEntry.Gui.Tooltip
    public boolean remote1 = true;
    @ConfigEntry.Gui.Tooltip
    public boolean remote2 = true;
    @ConfigEntry.Gui.Tooltip
    public boolean remote3 = true;

}
