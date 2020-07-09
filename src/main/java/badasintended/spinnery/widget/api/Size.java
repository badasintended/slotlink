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
 * Data class representing a width/height pair.
 */
public class Size implements WSized, JanksonSerializable {
	protected float width;
	protected float height;

	protected Size(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public static Size of(float width, float height) {
		return new Size(width, height);
	}

	public static Size of(float side) {
		return new Size(side, side);
	}

	/**
	 * Creates a copy of this Size object, incrementing its width and height by the supplied parameters.
	 *
	 * @return Size copy with incremented fields
	 */
	public Size add(float width, float height) {
		Size newSize = Size.of(this);
		return newSize.setWidth(newSize.getWidth() + width).setHeight(newSize.getHeight() + height);
	}

	/**
	 * Creates a new Size equivalent to that of the given WSized parameter.
	 *
	 * @return Size copy equivalent to given element
	 */
	public static Size of(WSized widget) {
		return new Size(widget.getWidth(), widget.getHeight());
	}

	public float getWidth() {
		return width;
	}

	public Size setWidth(float width) {
		this.width = width;
		return this;
	}

	public float getHeight() {
		return height;
	}

	public Size setHeight(float height) {
		this.height = height;
		return this;
	}

	public boolean isLargerInWidthAndHeight(Size size) {
		return this.width > size.width && this.height > size.height;
	}

	public boolean isLargerInWidthOrHeight(Size size) {
		return this.width > size.width || this.height > size.height;
	}

	public boolean isLargerInWidth(Size size) {
		return this.width > size.width;
	}

	public boolean isLargerInHeight(Size size) {
		return this.height > size.height;
	}

	public boolean isLargerInArea(Size size) {
		return this.width * this.height > size.width * size.height;
	}

	public boolean isSmallerInWidthAndHeight(Size size) {
		return this.width < size.width && this.height < size.height;
	}

	public boolean isSmallerInWidthOrHeight(Size size) {
		return this.width < size.width || this.height < size.height;
	}

	public boolean isSmallerInWidth(Size size) {
		return this.width < size.width;
	}

	public boolean isSmallerInHeight(Size size) {
		return this.height < size.height;
	}

	public boolean isSmallerInArea(Size size) {
		return this.width * this.height < size.width * size.height;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Size size = (Size) o;
		return getWidth() == size.getWidth() &&
				getHeight() == size.getHeight();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getWidth(), getHeight());
	}

	@Override
	public JsonElement toJson() {
		return JanksonUtilities.arrayOfPrimitives(width, height);
	}
}
