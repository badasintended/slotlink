/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.widget.WAbstractWidget;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that the annotated widget only listens for mouse events when focused, that is, when
 * {@link WAbstractWidget#isFocused()} returns true. This behavior is not inherited by child
 * classes!
 *
 * @deprecated Replaced with {@link WAbstractWidget#isFocusedMouseListener()}.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
public @interface WFocusedMouseListener {
}
