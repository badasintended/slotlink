package io.gitlab.intended.storagenetworks.registry;


import io.gitlab.intended.storagenetworks.inventory.CraftingTerminalInventory;
import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.container.Container;
import net.minecraft.util.Identifier;

public final class InventoryRegistry {

    public static void register() {
        register(BlockRegistry.CRAFTING_TERMINAL.ID, CraftingTerminalInventory::new);
    }

    protected static void register(Identifier id, ContainerFactory<Container> factory) {
        ContainerProviderRegistry.INSTANCE.registerFactory(id, factory);
    }

}
