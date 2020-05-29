package io.gitlab.intended.storagenetworks.registry;

import io.gitlab.intended.storagenetworks.StorageNetworks;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public final class ItemRegistry {

    public static void register() {
        
    }

    protected static void register(String id, Item item) {
        Registry.register(Registry.ITEM, StorageNetworks.id(id), item);
    }

}
