package badasintended.slotlink.datagen.provider

import badasintended.slotlink.init.Blocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTablesProvider

class LootTableProvider(dataGenerator: FabricDataGenerator) : FabricBlockLootTablesProvider(dataGenerator) {

    override fun generateBlockLootTables() {
        addDrop(Blocks.CABLE)
        addDrop(Blocks.EXPORT_CABLE)
        addDrop(Blocks.IMPORT_CABLE)
        addDrop(Blocks.LINK_CABLE)
        addDrop(Blocks.MASTER)
        addDrop(Blocks.REQUEST)
    }

}