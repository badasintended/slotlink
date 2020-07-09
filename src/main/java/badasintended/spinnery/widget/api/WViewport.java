/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing a "viewport", i.e. an object that has a larger underlying area, of which only
 * a part is visible.
 */
public interface WViewport {
	/**
	 * Retrieves the underlying size of this object.
	 *
	 * @return The underlying size of this object.
	 */
	Size getUnderlyingSize();

	/**
	 * Retrieves the underlying height of this object.
	 *
	 * @return The underlying height of this object.
	 */
	default float getUnderlyingHeight() {
		return getUnderlyingSize().getHeight();
	}

	/**
	 * Retrieves the underlying width of this object.
	 *
	 * @return The underlying width of this object.
	 */
	default float getUnderlyingWidth() {
		return getUnderlyingSize().getWidth();
	}

	/**
	 * Retrieves the visible size of this object.
	 *
	 * @return The visible size of this object.
	 */
	Size getVisibleSize();

	/**
	 * Retrieves the visible height of this object.
	 *
	 * @return The visible height of this object.
	 */
	default float getVisibleHeight() {
		return getVisibleSize().getHeight();
	}

	/**
	 * Retrieves the visible height of this object.
	 *
	 * @return The visible height of this object.
	 */
	default float getVisibleWidth() {
		return getVisibleSize().getWidth();
	}
}
