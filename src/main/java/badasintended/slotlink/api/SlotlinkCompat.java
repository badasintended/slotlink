package badasintended.slotlink.api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class SlotlinkCompat implements ModInitializer {

    public static SlotlinkCompat INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger("Slotlink Compat");

    private final Map<Class<? extends BlockEntity>, InventoryHandler<? extends BlockEntity>> HANDLERS = new HashMap<>();
    private final Set<Block> BLACKLIST = new HashSet<>();

    public final void registerBlacklist(Block... blocks) {
        BLACKLIST.addAll(Arrays.asList(blocks));
    }

    public final <T extends BlockEntity> void registerHandler(Class<T> clazz, InventoryHandler<T> inventoryHandler) {
        HANDLERS.put(clazz, inventoryHandler);
    }

    public static <T extends BlockEntity> boolean hasHandler(@Nullable T t) {
        if (t == null) return false;
        return INSTANCE.HANDLERS.containsKey(t.getClass());
    }

    @NotNull
    public static <T extends BlockEntity> Inventory getHandler(@NotNull T t) {
        return ((InventoryHandler<T>) INSTANCE.HANDLERS.get(t.getClass())).getHandler(t);
    }

    public static <T extends Block> boolean isBlacklisted(@NotNull T t) {
        return INSTANCE.BLACKLIST.contains(t);
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info("=== slotlink compatibility hell ===");
        FabricLoader.getInstance().getEntrypointContainers("slotlink-compat", SlotlinkCompatInitializer.class).forEach(val -> {
            SlotlinkCompatInitializer compat = val.getEntrypoint();
            String msg = " \"" + compat.getClass().getName() + "\" from \"" + val.getProvider().getMetadata().getId() + "\"";
            if (compat.dependencies().length == 0 || Arrays.stream(compat.dependencies()).allMatch(FabricLoader.getInstance()::isModLoaded)) {
                compat.initialize(INSTANCE);
                msg = "loaded" + msg;
            } else {
                msg = "ignored" + msg;
            }
            LOGGER.info(msg);
        });
        LOGGER.info("======== this is a mistake ========");
    }

}
