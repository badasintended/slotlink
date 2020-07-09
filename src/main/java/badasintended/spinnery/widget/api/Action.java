/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * An enumeration of common actions used by Spinnery.
 */
public enum Action {
	PICKUP,
	PICKUP_ALL,
	QUICK_MOVE,
	CLONE,

	DRAG_SPLIT,
	DRAG_SINGLE,
	DRAG_SPLIT_PREVIEW,
	DRAG_SINGLE_PREVIEW;

	public enum Subtype {
		FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK,
		FROM_CURSOR_TO_SLOT_DEFAULT_FULL_STACK,
		FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM,

		FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK,
		FROM_SLOT_TO_CURSOR_DEFAULT_HALF_STACK,
		FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK
	}

	public static Action of(int button, boolean mode) {
		switch (button) {
			case 0:
				return mode ? DRAG_SPLIT : DRAG_SPLIT_PREVIEW;
			case 1:
				return mode ? DRAG_SINGLE : DRAG_SINGLE_PREVIEW;
			default:
				return null;
		}
	}

	public boolean isPreview() {
		return this == DRAG_SINGLE_PREVIEW || this == DRAG_SPLIT_PREVIEW;
	}

	public boolean isSplit() {
		return this == DRAG_SPLIT || this == DRAG_SPLIT_PREVIEW;
	}

	public boolean isSingle() {
		return this == DRAG_SINGLE || this == DRAG_SINGLE_PREVIEW;
	}
}
