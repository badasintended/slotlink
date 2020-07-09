/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.client.integration;

import badasintended.spinnery.Spinnery;
import badasintended.spinnery.client.configuration.screen.ConfigurationScreen;
import badasintended.spinnery.common.configuration.data.ConfigurationHolder;
import badasintended.spinnery.common.configuration.data.ConfigurationOption;
import badasintended.spinnery.common.configuration.registry.ConfigurationRegistry;

public class SpinneryConfigurationScreen extends ConfigurationScreen {
	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<String> preferredTheme = new ConfigurationHolder<>("spinnery:default");

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Boolean> smoothing = new ConfigurationHolder<>(true);

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Boolean> arrows = new ConfigurationHolder<>(true);

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Boolean> fading = new ConfigurationHolder<>(true);

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Float> kineticReductionCoefficient = new ConfigurationHolder<>(1.1f);

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Float> kineticAccelerationCoefficient = new ConfigurationHolder<>(1.5f);

	@ConfigurationOption(name = Spinnery.MOD_ID)
	public static final ConfigurationHolder<Float> dragScrollAccelerationCoefficient = new ConfigurationHolder<>(0.0005f);

	public static void initialize() {
		// NO-OP
	}

	static {
		name = Spinnery.MOD_ID;

		ConfigurationRegistry.register(SpinneryConfigurationScreen.class);
	}
}
