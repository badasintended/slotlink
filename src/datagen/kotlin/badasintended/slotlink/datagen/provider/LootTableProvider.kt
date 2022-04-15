package badasintended.slotlink.datagen.provider

import badasintended.slotlink.init.Blocks
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider

class LootTableProvider(dataGenerator: FabricDataGenerator) : FabricBlockLootTableProvider(dataGenerator) {

    override fun generateBlockLootTables() {
        addDrop(Blocks.CABLE)
        addDrop(Blocks.EXPORT_CABLE)
        addDrop(Blocks.IMPORT_CABLE)
        addDrop(Blocks.LINK_CABLE)
        addDrop(Blocks.MASTER)
        addDrop(Blocks.REQUEST)
        addDrop(Blocks.INTERFACE)
    }

}