package badasintended.slotlink.client.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader

private val configFile = FabricLoader.getInstance().configDir.resolve("slotlink.json").toFile()
private val json = Json {
    encodeDefaults = true
    prettyPrint = true
}

val config: SlotlinkConfig by lazy {
    val conf = if (configFile.exists()) json.decodeFromString(configFile.readText()) else SlotlinkConfig()
    conf.save()
    conf
}

@Serializable
class SlotlinkConfig(
    var showCraftingGrid: Boolean = true,
    var syncReiSearch: Boolean = false
) {

    fun save() {
        configFile.writeText(json.encodeToString(this))
    }

}

