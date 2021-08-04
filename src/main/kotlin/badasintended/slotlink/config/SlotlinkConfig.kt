package badasintended.slotlink.config

import badasintended.slotlink.screen.RequestScreenHandler.SortMode
import badasintended.slotlink.util.log
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
    var syncReiSearch: Boolean = false,
    var pauseTransferWhenOnScreen: Boolean = true
) {

    fun save() {
        configFile.writeText(json.encodeToString(this))
    }

    fun invalidate() {
        json.decodeFromString<SlotlinkConfig>(configFile.readText()).also {
            autoFocusSearchBar = it.autoFocusSearchBar
            showCraftingGrid = it.showCraftingGrid
            sort = it.sort
            syncReiSearch = it.syncReiSearch
            pauseTransferWhenOnScreen = it.pauseTransferWhenOnScreen
        }
        log.info("slotlink config invalidated")
    }

}

