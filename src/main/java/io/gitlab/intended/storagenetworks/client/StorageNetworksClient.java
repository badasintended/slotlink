package io.gitlab.intended.storagenetworks.client;

import io.gitlab.intended.storagenetworks.client.registry.ScreenRegistry;
import net.fabricmc.api.ClientModInitializer;

public class StorageNetworksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register();
    }

}
