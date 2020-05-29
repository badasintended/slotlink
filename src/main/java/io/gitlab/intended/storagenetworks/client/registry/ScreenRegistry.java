package io.gitlab.intended.storagenetworks.client.registry;

import io.gitlab.intended.storagenetworks.client.gui.screen.CraftingTerminalScreen;
import io.gitlab.intended.storagenetworks.registry.BlockRegistry;
import net.fabricmc.fabric.api.client.screen.ContainerScreenFactory;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.Container;
import net.minecraft.util.Identifier;

public final class ScreenRegistry {

    public static void register() {
        register(BlockRegistry.CRAFTING_TERMINAL.ID, CraftingTerminalScreen::new);
    }

    protected static <C extends Container> void register(Identifier id, ContainerScreenFactory<C> factory) {
        ScreenProviderRegistry.INSTANCE.registerFactory(id, factory);
    }

}
