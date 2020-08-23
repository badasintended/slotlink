package badasintended.slotlink.api;

import org.jetbrains.annotations.NotNull;

/**
 * owo what's this
 */
public interface SlotlinkCompatInitializer {

    /**
     * Declare your dependencies here.
     * Not needed if the impl is on the target mod.
     */
    default String[] dependencies() {
        return new String[0];
    }

    void initialize(@NotNull Compat compat);

}
