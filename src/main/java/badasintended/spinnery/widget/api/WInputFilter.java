/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

/**
 * An input filter for widgets where one
 * is necessary.
 *
 * @param <T> the output value of this filter.
 */
public interface WInputFilter<T> {
	/**
	 * Convert an output value to a String.
	 * @param t the given output value.
	 * @return the requested string.
	 */
	String toString(T t);

	/**
	 * Convert a String to an output value.
	 * @param text the given String.
	 * @return the requested output value.
	 */
	T toValue(String text);

	/**
	 * Asserts whether this filter accepts the
	 * given input or not.
	 * @param character the character typed.
	 * @param text the full text for context.
	 * @return the requested boolean.
	 */
	boolean accepts(String character, String text);
}
