package badasintended.slotlink.config

import badasintended.slotlink.screen.RequestScreenHandler.SortMode
import badasintended.slotlink.util.log
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader

private val configFile = FabricLoader.getInstance().configDir.resolve("slotlink.json").toFile()
private val json = Json {
    encodeDefaults = true
    prettyPrint = true
    ignoreUnknownKeys = true
}

val config: SlotlinkConfig by lazy {
    val conf = if (configFile.exists()) json.decodeFromString(configFile.readText()) else SlotlinkConfig()
    log.info("slotlink config loaded")
    conf.save()
    conf
}

@Serializable
class SlotlinkConfig(
    var autoFocusSearchBar: Boolean = false,
    var showCraftingGrid: Boolean = true,
    var sort: SortMode = SortMode.COUNT_DESC,
    var syncRecipeViewerSearch: Boolean = false,
    var pauseTransferWhenOnScreen: Boolean = false,
    var tryMergeStack: Boolean = true
) {

    fun save() {
        configFile.writeText(json.encodeToString(this))
    }

    fun invalidate() {
        json.decodeFromString<SlotlinkConfig>(configFile.readText()).also {
            for (property in SlotlinkConfig::class.memberProperties) if (property is KMutableProperty<*>) {
                property.setter.call(this, property.get(it))
            }
        }
        log.info("slotlink config invalidated")
    }

}

