/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.widget.WAbstractWidget;

/**
 * Generic interface that describes a context lock for an object,
 * allowing actions to be taken based on the lock state.
 */
public interface WContextLock {
	boolean isActive();

	<W extends WAbstractWidget> W setActive(boolean active);
}
