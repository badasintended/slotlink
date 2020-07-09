/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.widget.WAbstractWidget;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Generic interface representing a collection of widgets. This interface does not presume the ability to
 * add or remove widgets from the collection, and simply provides accessors to the collection's children.
 *
 * @author EngiN33R
 * @see WModifiableCollection
 * @since 2.0.0
 */
public interface WCollection {
	/**
	 * Returns a Set of widgets that are direct children of this collection.
	 *
	 * @return set of direct child widgets
	 * @author EngiN33R
	 * @since 2.0.0
	 */
	Set<WAbstractWidget> getWidgets();

	/**
	 * Returns a Set of all widgets contained in this collection, including those in nested collections.
	 * The default implementation of this method does not check for cyclic references, so having the root
	 * collection as a child of any other collection within it will produce a {@link StackOverflowError}.
	 *
	 * @return set of all child widgets
	 * @author EngiN33R
	 * @since 2.0.0
	 */
	default Set<WAbstractWidget> getAllWidgets() {
		Set<WAbstractWidget> allWidgets = new LinkedHashSet<>(getWidgets());
		for (WAbstractWidget widget : getWidgets()) {
			if (widget instanceof WCollection) {
				allWidgets.addAll(((WCollection) widget).getAllWidgets());
			}
		}
		return allWidgets;
	}

	/**
	 * Checks whether the specified widgets are contained within this collection.
	 *
	 * @param widgets widgets to check
	 * @return whether widgets are contained
	 */
	boolean contains(WAbstractWidget... widgets);
}
