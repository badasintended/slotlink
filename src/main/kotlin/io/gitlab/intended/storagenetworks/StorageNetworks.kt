package io.gitlab.intended.storagenetworks

import io.gitlab.intended.storagenetworks.block.ModBlocks
import io.gitlab.intended.storagenetworks.block.entity.ModBlockEntities
import io.gitlab.intended.storagenetworks.inventory.ModInventories
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import java.util.logging.Level
import java.util.logging.Logger

object StorageNetworks : ModInitializer {

    const val ID = "storagenetworks"
    const val NAME = "Storage Networks"

    val LOGGER: Logger = Logger.getLogger(ID)

    val config: ModConfig get() = AutoConfig.getConfigHolder(ModConfig::class.java).config

    fun id(path: String) = Identifier(ID, path)
    fun log(level: Level, msg: String) = LOGGER.log(level, "[$NAME] $msg")

    override fun onInitialize() {
        AutoConfig.register(ModConfig::class.java) { definition, configClass -> JanksonConfigSerializer(definition, configClass) }

        ModBlocks.init()
        ModBlockEntities.init()
        ModInventories.init()
    }

}
