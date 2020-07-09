/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing a container that may be scrolled.
 */
public interface WScrollable extends WViewport {
	/**
	 * Scrolls the contents by the specified X and Y deltas. This operation should result in the contents being
	 * offset by that amount.
	 *
	 * @param deltaX amount to scroll by along X
	 * @param deltaY amount to scroll by along Y
	 */
	void scroll(double deltaX, double deltaY);
}
