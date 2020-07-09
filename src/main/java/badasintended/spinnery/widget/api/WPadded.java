/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * Utility interface building on {@link WInnerSized} that enables simple use of a {@link Padding} value to provide
 * the inner anchor and size.
 */
public interface WPadded extends WInnerSized {
	@Override
	default Size getInnerSize() {
		return Size.of(this).add(
				-(getPadding().getLeft() + getPadding().getRight()),
				-(getPadding().getTop() + getPadding().getBottom())
		);
	}

	Padding getPadding();

	@Override
	default Position getInnerAnchor() {
		return Position.of(this, getPadding().getLeft(), getPadding().getTop());
	}
}
