/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing an object that may have a width and height. Utility classes are well-served by this
 * interface; other use cases should probably implement a less generic interface, such as {@link WLayoutElement}.
 */
public interface WSized {
	/**
	 * Retrieves the width of this object.
	 *
	 * @return The width of this object.
	 */
	default float getWidth() {
		return 0;
	}

	/**
	 * Retrieves the height of this object.
	 *
	 * @return The height of this object.
	 */
	default float getHeight() {
		return 0;
	}
}
