package io.gitlab.intended.storagenetworks

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.block.entity.ModBlockEntities
import io.gitlab.intended.storagenetworks.client.gui.screen.ModScreens
import io.gitlab.intended.storagenetworks.inventory.ModInventories
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Identifier
import java.util.logging.Level
import java.util.logging.Logger

object Mod {

    const val ID = "storagenetworks"
    const val NAME = "Storage Networks"

    val LOGGER: Logger = Logger.getLogger(ID)

    val config: ModConfig get() = AutoConfig.getConfigHolder(ModConfig::class.java).config

    fun id(path: String) = Identifier(ID, path)
    fun log(level: Level, msg: String) = LOGGER.log(level, "[$NAME] $msg")

    @Suppress("unused")
    fun main() {
        AutoConfig.register(ModConfig::class.java) { def, clazz -> JanksonConfigSerializer(def, clazz) }

        ModBlocks.init()
        ModBlockEntities.init()
        ModInventories.init()
    }

    @Suppress("unused")
    @Environment(EnvType.CLIENT)
    fun client() {
        ModScreens.init()
    }

}


@Suppress("unused")
@Environment(EnvType.CLIENT)
object ModMenu : ModMenuApi {

    override fun getModId() = Mod.ID

    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent: Screen ->
        AutoConfig.getConfigScreen(ModConfig::class.java, parent).get()
    }

}


@Config(name = Mod.ID)
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
