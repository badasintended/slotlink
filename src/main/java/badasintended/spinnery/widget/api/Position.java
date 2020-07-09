/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget.api;

import badasintended.spinnery.common.utility.JanksonUtilities;
import blue.endless.jankson.JsonElement;

import java.util.Objects;

/**
 * Data class representing a position offset relative to an anchor. By default, the anchor is ORIGIN,
 * a position with all 0 coordinates.
 */
public class Position implements WPositioned, JanksonSerializable {
	public static final Position ORIGIN = new Position(new WPositioned() {
		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public float getZ() {
			return 0;
		}
	});

	protected WPositioned anchor;
	protected float x;
	protected float y;
	protected float z;
	protected float offsetX;
	protected float offsetY;
	protected float offsetZ;

	protected Position(WPositioned anchor) {
		this.anchor = anchor;
	}

	public static Position origin() {
		return new Position(ORIGIN);
	}

	/**
	 * Creates a position with the given "absolute" (relative to ORIGIN) coordinates.
	 *
	 * @param x absolute x
	 * @param y absolute y
	 * @param z absolute z
	 */
	public static Position of(float x, float y, float z) {
		return new Position(ORIGIN).set(x, y, z);
	}

	/**
	 * Sets new coordinates of this position object relative to its anchor.
	 *
	 * @param x relative x
	 * @param y relative y
	 * @param z relative z
	 * @return same position object
	 */
	public Position set(float x, float y, float z) {
		setRelativeX(x);
		setRelativeY(y);
		setRelativeZ(z);
		return this;
	}

	/**
	 * Creates a position with the given coordinates relative to anchor.
	 *
	 * @param anchor anchor
	 * @param x      relative x
	 * @param y      relative y
	 * @param z      relative z
	 */
	public static Position of(WPositioned anchor, float x, float y, float z) {
		return new Position(anchor).set(x, y, z);
	}

	/**
	 * Creates a position with the given coordinates relative to anchor and 0 Z offset.
	 *
	 * @param anchor anchor
	 * @param x      relative x
	 * @param y      relative y
	 */
	public static Position of(WPositioned anchor, float x, float y) {
		return new Position(anchor).set(x, y, 0);
	}

	/**
	 * Creates a position equivalent to the top-right corner of the given layout element (anchor + element width).
	 *
	 * @param source layout element
	 */
	public static Position ofTopRight(WLayoutElement source) {
		return Position.of(source).add(source.getWidth(), 0, 0);
	}

	/**
	 * Copies this position object and increments its coordinates by the given parameters.
	 *
	 * @param x increment relative x
	 * @param y increment relative y
	 * @param z increment relative z
	 * @return new position object
	 */
	public Position add(float x, float y, float z) {
		Position newPos = Position.of(this);
		newPos.set(newPos.x + x, newPos.y + y, newPos.z + z);
		return newPos;
	}

	/**
	 * Creates a copy of the given positioned element's position.
	 *
	 * @param source positioned element
	 * @return equivalent position
	 */
	public static Position of(WPositioned source) {
		return new Position(source);
	}

	/**
	 * Creates a position equivalent to the bottom-left corner of the given layout element (anchor + element height).
	 *
	 * @param source layout element
	 */
	public static Position ofBottomLeft(WLayoutElement source) {
		return Position.of(source).add(0, source.getHeight(), 0);
	}

	/**
	 * Creates a position equivalent to the bottom-right corner of the given layout element (anchor + element size).
	 *
	 * @param source layout element
	 */
	public static Position ofBottomRight(WLayoutElement source) {
		return Position.of(source).add(source.getWidth(), source.getHeight(), 0);
	}

	/**
	 * Sets new offset coordiantes of this position object.
	 *
	 * @param x offset x
	 * @param y offset y
	 * @param z offset z
	 * @return same position object
	 */
	public Position setOffset(float x, float y, float z) {
		setOffsetX(x);
		setOffsetY(y);
		setOffsetZ(z);
		return this;
	}

	public WPositioned getAnchor() {
		return anchor;
	}

	public Position setAnchor(WPositioned anchor) {
		this.anchor = anchor;
		return this;
	}

	/**
	 * Gets the absolute X coordinate, which is calculated as the sum of the anchor's coordinate, this
	 * position's relative coordiate, and this position's offset coordiante.
	 *
	 * @return absolute coordinate
	 */
	public float getX() {
		return anchor.getX() + x + offsetX;
	}

	/**
	 * Gets the absolute Y coordinate, which is calculated as the sum of the anchor's coordinate, this
	 * position's relative coordiate, and this position's offset coordiante.
	 *
	 * @return absolute coordinate
	 */
	public float getY() {
		return anchor.getY() + y + offsetY;
	}

	/**
	 * Gets the absolute Y coordinate, which is calculated as the sum of the anchor's coordinate, this
	 * position's relative coordiate, and this position's offset coordiante.
	 *
	 * @return absolute coordinate
	 */
	public float getZ() {
		return anchor.getZ() + z + offsetZ;
	}

	/**
	 * Changes this position objects's relative Z coordinate in such a way that its absolute X coordinate will be
	 * equal to the parameter.
	 *
	 * @param z absolute coordinate
	 * @return same position object
	 */
	public Position setZ(float z) {
		return setRelativeZ(z - anchor.getZ() - offsetZ);
	}

	/**
	 * Changes this position objects's relative Y coordinate in such a way that its absolute X coordinate will be
	 * equal to the parameter.
	 *
	 * @param y absolute coordinate
	 * @return same position object
	 */
	public Position setY(float y) {
		return setRelativeY(y - anchor.getY() - offsetY);
	}

	/**
	 * Changes this position objects's relative X coordinate in such a way that its absolute X coordinate will be
	 * equal to the parameter.
	 *
	 * @param x absolute coordinate
	 * @return same position object
	 */
	public Position setX(float x) {
		return setRelativeX(x - anchor.getX() - offsetX);
	}

	public float getRelativeX() {
		return x;
	}

	public Position setRelativeX(float x) {
		this.x = x;
		return this;
	}

	public float getRelativeY() {
		return y;
	}

	public Position setRelativeY(float y) {
		this.y = y;
		return this;
	}

	public float getRelativeZ() {
		return z;
	}

	public Position setRelativeZ(float offsetZ) {
		this.z = offsetZ;
		return this;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public Position setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public Position setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	public float getOffsetZ() {
		return offsetZ;
	}

	public Position setOffsetZ(float offsetZ) {
		this.offsetZ = offsetZ;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return getX() == position.getX() &&
				getY() == position.getY() &&
				getZ() == position.getZ();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAnchor(), getX(), getY(), getZ());
	}

	@Override
	public JsonElement toJson() {
		return JanksonUtilities.arrayOfPrimitives(x, y, z);
	}
}
