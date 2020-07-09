/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.utility;

import badasintended.spinnery.Spinnery;
import badasintended.spinnery.common.configuration.registry.ConfigurationRegistry;
import badasintended.spinnery.common.registry.ThemeResourceRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

/**
 * A basic implementation of
 * a resource listener for
 * Spinnery's resources/themes.
 */
@Environment(EnvType.CLIENT)
public class ResourceListener implements SimpleSynchronousResourceReloadListener {
	private static final Identifier ID = new Identifier(Spinnery.MOD_ID, "reload_listener");

	@Override
	public void apply(ResourceManager resourceManager) {
		ThemeResourceRegistry.clear();
		ThemeResourceRegistry.load(resourceManager);

		ConfigurationRegistry.load(resourceManager);
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}
}
