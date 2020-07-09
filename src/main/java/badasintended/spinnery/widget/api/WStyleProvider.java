/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing an object that may provide styles.
 */
public interface WStyleProvider {
	/**
	 * Retrieves the Style of this object.
	 *
	 * @return The Style of this object.
	 */
	Style getStyle();
}
