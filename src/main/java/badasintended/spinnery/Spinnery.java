/*
 * This code is taken from Spinnery project v3.0.48, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery/blob/63e4290e3a2efe12c6265151bb3fbdc90374a979)
 */

package badasintended.spinnery;

import badasintended.slotlink.Slotlink;
import badasintended.spinnery.common.configuration.registry.ConfigurationRegistry;
import badasintended.spinnery.common.registry.NetworkRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spinnery implements ModInitializer {
	public static final String LOG_ID = "Spinnery";
	public static final String MOD_ID = Slotlink.ID;
	public static Logger LOGGER = LogManager.getLogger("Spinnery");

	@Override
	public void onInitialize() {
		NetworkRegistry.initialize();
		ConfigurationRegistry.initialize();
	}
}
