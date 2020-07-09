/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.widget.WInterface;

/**
 * Generic interface for providing a widget interface. Widget interface providers are generally
 * various implementations of screens.
 */
public interface WInterfaceProvider {
	WInterface getInterface();
}
