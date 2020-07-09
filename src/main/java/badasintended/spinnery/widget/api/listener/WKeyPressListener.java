/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api.listener;

import badasintended.spinnery.widget.WAbstractWidget;

/**
 * An interface for events called when a key is pressed.
 */
public interface WKeyPressListener<W extends WAbstractWidget> {
	void event(W widget, int keyPressed, int character, int keyModifier);
}
