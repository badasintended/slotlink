/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import net.minecraft.util.Identifier;

/**
 * Generic interface representing an object that may have a theme.
 */
public interface WThemable {
	/**
	 * Retrieves the Theme Identifier of this object.
	 *
	 * @return The Theme Identifier of this object.
	 */
	Identifier getTheme();
}
