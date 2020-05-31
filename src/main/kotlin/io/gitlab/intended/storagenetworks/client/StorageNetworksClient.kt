package io.gitlab.intended.storagenetworks.client

import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import io.gitlab.intended.storagenetworks.ModConfig
import io.gitlab.intended.storagenetworks.StorageNetworks
import io.gitlab.intended.storagenetworks.client.gui.screen.ModScreens
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.gui.screen.Screen


object StorageNetworksClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModScreens.init()
    }

}


object ModMenuIntegration : ModMenuApi {

    override fun getModId() = StorageNetworks.ID

    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent: Screen ->
        AutoConfig.getConfigScreen(ModConfig::class.java, parent).get()
    }

}