package badasintended.slotlink.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Compat implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Slotlink Compat");

    private static Compat instance;

    private final Map<Class<? extends BlockEntity>, InventoryHandler<? extends BlockEntity>> handlers = new HashMap<>();
    private final Set<Block> blacklist = new HashSet<>();

    /**
     * Register blocks that will be ignored by link cables.
     */
    public final void registerBlacklist(Block... blocks) {
        blacklist.addAll(Arrays.asList(blocks));
    }

    /**
     * Register custom item handling.
     * all its child class will be handled the same.
     */
    public final <T extends BlockEntity> void registerHandler(Class<T> clazz, InventoryHandler<T> inventoryHandler) {
        handlers.put(clazz, inventoryHandler);
    }

    @Nullable
    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> Class<? extends BlockEntity> getRegisteredClass(@Nullable T t) {
        if (t == null) return null;
        Class<?> clazz = t.getClass();
        boolean containsKey = instance.handlers.containsKey(clazz);

        if (!containsKey) do {
            clazz = clazz.getSuperclass();
            containsKey = instance.handlers.containsKey(clazz);
        } while (!containsKey && clazz != BlockEntity.class);

        if (containsKey) {
            instance.handlers.putIfAbsent(t.getClass(), instance.handlers.get(clazz));
            return (Class<? extends BlockEntity>) clazz;
        }
        return null;
    }

    @NotNull
    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public static <V extends BlockEntity, T extends BlockEntity> Inventory getHandler(Class<V> key, T t) {
        return ((InventoryHandler<V>) instance.handlers.get(key)).getHandler((V) t);
    }

    @ApiStatus.Internal
    public static <T extends Block> boolean isBlacklisted(@NotNull T t) {
        return instance.blacklist.contains(t);
    }

    @Override
    public void onInitialize() {
        instance = this;
        FabricLoader.getInstance().getEntrypointContainers("slotlink-compat", SlotlinkCompatInitializer.class).forEach(val -> {
            SlotlinkCompatInitializer compat = val.getEntrypoint();
            String msg = " \"" + compat.getClass().getName() + "\" from \"" + val.getProvider().getMetadata().getId() + "\"";
            if (compat.dependencies().length == 0 || Arrays.stream(compat.dependencies()).allMatch(FabricLoader.getInstance()::isModLoaded)) {
                compat.initialize(instance);
                msg = "loaded" + msg;
            } else {
                msg = "ignored" + msg;
            }
            LOGGER.info(msg);
        });
    }

}
