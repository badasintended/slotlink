package io.gitlab.intended.storagenetworks

import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry

@Config(name = StorageNetworks.ID)
@Config.Gui.Background("${StorageNetworks.ID}:textures/gui/config.png")
class ModConfig : ConfigData {

    @ConfigEntry.Gui.Tooltip
    var remote0 = true
    @ConfigEntry.Gui.Tooltip
    var remote1 = true
    @ConfigEntry.Gui.Tooltip
    var remote2 = true
    @ConfigEntry.Gui.Tooltip
    var remote3 = true

}