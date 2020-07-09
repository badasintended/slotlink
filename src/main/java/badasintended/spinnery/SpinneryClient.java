/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery;

import badasintended.spinnery.client.integration.SpinneryConfigurationScreen;
import badasintended.spinnery.common.registry.NetworkRegistry;
import badasintended.spinnery.common.registry.ThemeResourceRegistry;
import badasintended.spinnery.common.registry.WidgetRegistry;
import net.fabricmc.api.ClientModInitializer;

public class SpinneryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		NetworkRegistry.initializeClient();
		WidgetRegistry.initialize();
		ThemeResourceRegistry.initialize();

		SpinneryConfigurationScreen.initialize();
	}
}
