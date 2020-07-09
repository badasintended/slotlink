/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.widget.WAbstractWidget;

import java.util.List;

/**
 * Generic interface describing a collection of layout elements to be drawn to the canvas. To this end, the interface
 * provides {@link #getOrderedWidgets()} that returns a List of widgets in drawing order. Additionally, implementing
 * classes are encouraged to use caching for Z ordering - to this end, {@link #recalculateCache()} is provided as
 * a standard method to recalculate the Z-ordered cache.
 */
public interface WDrawableCollection extends WCollection {
	/**
	 * Recalculates the cached collection of Z-ordered layout elements. Z ordering should be a cacheable operation, as
	 * it can be an expensive operation with a large number of children. This method should generally be called in
	 * {@link WAbstractWidget#onLayoutChange()} or under similar conditions in order to maximise efficiency.
	 */
	void recalculateCache();

	/**
	 * Returns a List of layout elements to be drawn. Layout elements nearer to the end of the list are drawn on top
	 * of those nearer to its beginning. According to Spinnery conventions, layout elements in the returned list should
	 * be ordered in ascending Z order, i.e. larger Z means topmost drawing.
	 *
	 * @return list of layout elements in drawing order
	 */
	List<WLayoutElement> getOrderedWidgets();
}
