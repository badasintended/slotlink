package badasintended.slotlink.api;

/**
 * owo what's this
 */
public interface SlotlinkCompatInitializer {

    default String[] dependencies() {
        return new String[0];
    }

    void initialize(SlotlinkCompat compat);

}
