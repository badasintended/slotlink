/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing an object that provides a position.
 *
 * <p>This interface is useful for creating methods that should be able to extract positions from variously
 * typed objects, e.g. {@link WLayoutElement}, {@link Position} and others. It is generally inadvisable to
 * implement this interface directly in your classes; instead, consider using a less generic counterpart that
 * provides more convenience methods and standard logic.
 */
public interface WPositioned {
	/**
	 * Retrieves the X position of this object.
	 *
	 * @return The X position of this object.
	 */
	default float getX() {
		return 0;
	}

	/**
	 * Retrieves the Y position of this object.
	 *
	 * @return The Y position of this object.
	 */
	default float getY() {
		return 0;
	}

	/**
	 * Retrieves the Z position of this object.
	 *
	 * @return The Z position of this object.
	 */
	default float getZ() {
		return 0;
	}
}
