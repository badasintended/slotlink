/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.utility;

import java.util.function.Consumer;

public class MutablePair<L, R> {
	private L first;
	private R second;

	/**
	 * Builds a MutablePair of the given elements.
	 *
	 * @return Mutable pair of the given elements.
	 */
	public static <A, B> MutablePair<A, B> of(A first, B second) {
		MutablePair<A, B> pair = new MutablePair<>();
		pair.first = first;
		pair.second = second;
		return pair;
	}

	/**
	 * Retrieves the first member of this pair.
	 *
	 * @return The first member of this pair.
	 */
	public L getFirst() {
		return first;
	}

	/**
	 * Sets the first member of this pair.
	 *
	 * @param first Object to be set as first member.
	 */
	public MutablePair setFirst(L first) {
		this.first = first;
		return this;
	}

	/**
	 * Retrieves the second member of this pair.
	 *
	 * @return The second member of this pair.
	 */
	public R getSecond() {
		return second;
	}

	/**
	 * Sets the second member of this pair.
	 *
	 * @param second Object to be set as second member.
	 */
	public MutablePair setSecond(R second) {
		this.second = second;
		return this;
	}

	/**
	 * Applies one consumer for each member of this pair.
	 *
	 * @param l Consumer to apply to first member.
	 * @param r Consumer to apply to second value.
	 */
	public MutablePair apply(Consumer<L> l, Consumer<R> r) {
		l.accept(first);
		r.accept(second);
		return this;
	}
}
