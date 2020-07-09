/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Generic interface representing a container that may be scrolled vertically.
 */
public interface WVerticalScrollable extends WScrollable {
	/**
	 * Gets the Y coordinate of the start anchor. When containing widgets are positioned at the start anchor,
	 * the first widgets should be visible, and the scrollable is considered to be scrolled to the start.
	 *
	 * @return start anchor Y position
	 */
	float getStartAnchorY();

	/**
	 * Gets the Y coordinate of the end anchor. When containing widgets are positioned at the end anchor,
	 * the last widgets should be visible, and the scrollable is considered to be scrolled to the end.
	 *
	 * @return end anchor Y position
	 */
	float getEndAnchorY();

	/**
	 * Gets the Y offset from the start anchor. This offset represents how far from the start anchor along
	 * the Y axis the contained widgets have been scrolled.
	 *
	 * @return Y offset from start anchor
	 */
	float getStartOffsetY();
}
