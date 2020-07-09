/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.widget;

import badasintended.spinnery.common.registry.ThemeRegistry;
import badasintended.spinnery.common.registry.WidgetRegistry;
import badasintended.spinnery.common.utility.EventUtilities;
import badasintended.spinnery.widget.api.*;
import badasintended.spinnery.widget.api.listener.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;

/**
 * A WAbstractWidget provides the base functionality
 * needed by any widget. Such includes events,
 * positions, sizes, utility methods, and much more.
 * It is extended by all widgets.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class WAbstractWidget implements Tickable, WLayoutElement, WThemable, WStyleProvider, WEventListener {
	protected WInterface linkedInterface;
	protected WLayoutElement parent;

	protected Position position = Position.origin();

	protected Size size = Size.of(0, 0);
	protected Size baseAutoSize = Size.of(0, 0);
	protected Size minimumAutoSize = Size.of(0, 0);
	protected Size maximumAutoSize = Size.of(Integer.MAX_VALUE, Integer.MAX_VALUE);

	protected Text label = new LiteralText("");

	protected boolean isHidden = false;
	protected boolean hasFocus = false;
	protected boolean isHeld = false;

	protected long heldSince = 0;

	protected WCharTypeListener runnableOnCharTyped;
	protected WMouseClickListener runnableOnMouseClicked;
	protected WKeyPressListener runnableOnKeyPressed;
	protected WKeyReleaseListener runnableOnKeyReleased;
	protected WFocusGainListener runnableOnFocusGained;
	protected WFocusLossListener runnableOnFocusReleased;
	protected WTooltipDrawListener runnableOnDrawTooltip;
	protected WMouseReleaseListener runnableOnMouseReleased;
	protected WMouseMoveListener runnableOnMouseMoved;
	protected WMouseDragListener runnableOnMouseDragged;
	protected WMouseScrollListener runnableOnMouseScrolled;
	protected WAlignListener runnableOnAlign;

	protected Identifier theme;
	protected Style styleOverrides = new Style();

	public WAbstractWidget() {
	}

	/**
	 * Retrieves the interface attached to this widget.
	 *
	 * @return The interface attached to this widget.
	 */
	public WInterface getInterface() {
		return linkedInterface;
	}

	/**
	 * Sets the interface attached to this widget.
	 *
	 * @param linkedInterface Interface to be attached to this widget.
	 */
	public <W extends WAbstractWidget> W setInterface(WInterface linkedInterface) {
		this.linkedInterface = linkedInterface;
		return (W) this;
	}

	@Override
	public void tick() {
	}

	/**
	 * Asserts whether this widget has a label or not.
	 *
	 * @return True if labeled; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean hasLabel() {
		return !label.getString().isEmpty();
	}

	/**
	 * Retrieves this widget's label.
	 *
	 * @return This widget's label.
	 */
	@Environment(EnvType.CLIENT)
	public Text getLabel() {
		return label;
	}

	/**
	 * Sets this widget's label as a Text (any type).
	 *
	 * @param label Label to be used by this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setLabel(Text label) {
		this.label = label;
		onLayoutChange();
		return (W) this;
	}

	/**
	 * Sets this widget's label as a String (formatted into LiteralText).
	 *
	 * @param label Label to be used by this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setLabel(String label) {
		this.label = new LiteralText(label);
		onLayoutChange();
		return (W) this;
	}

	/**
	 * Asserts whether this widget's label is shadowed or not.
	 *
	 * @return True if shadowed; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isLabelShadowed() {
		return getStyle().asBoolean("label.shadow");
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Style getStyle() {
		Identifier widgetId = WidgetRegistry.getId(getClass());
		if (widgetId == null) {
			Class superClass = getClass().getSuperclass();
			while (superClass != Object.class) {
				widgetId = WidgetRegistry.getId(superClass);
				if (widgetId != null) break;
				superClass = superClass.getSuperclass();
			}
		}
		return Style.of(ThemeRegistry.getStyle(getTheme(), widgetId)).mergeFrom(styleOverrides);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Identifier getTheme() {
		if (theme != null) return theme;
		if (parent != null && parent instanceof WThemable) return ((WThemable) parent).getTheme();
		if (linkedInterface != null && linkedInterface.getTheme() != null)
			return linkedInterface.getTheme();
		return ThemeRegistry.DEFAULT_THEME;
	}

	/**
	 * Sets the theme associated with this widget as an Identifier.
	 *
	 * @param theme Theme to be used by this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setTheme(Identifier theme) {
		this.theme = theme;
		return (W) this;
	}

	/**
	 * Sets the theme associated with this widget  as a String (formatted into Identifier).
	 *
	 * @param theme Theme to be used by this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setTheme(String theme) {
		return setTheme(new Identifier(theme));
	}

	/**
	 * Method called when the widget's position needs to be realigned with its anchors, if any.
	 */
	@Environment(EnvType.CLIENT)
	public void align() {
	}

	/**
	 * Method called which centers the widget's position in the horizontal (X) and vertical (Y) axis relative to the screen.
	 */
	@Environment(EnvType.CLIENT)
	public void center() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2)
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	/**
	 * Retrieves this widget's position.
	 *
	 * @return This widget's position.
	 */
	@Environment(EnvType.CLIENT)
	public Position getPosition() {
		return position;
	}

	/**
	 * Retrieves this widget's parent.
	 *
	 * @return This widget's parent.
	 */
	@Environment(EnvType.CLIENT)
	public WLayoutElement getParent() {
		return parent;
	}

	/**
	 * Method called when a change happens in the widget layout of the current interface, which propagates to all parents.
	 */
	@Override
	public void onLayoutChange() {
		if (parent != null) parent.onLayoutChange();
	}

	/**
	 * Sets this widget's parent element.
	 *
	 * @param parent Element to be used as parent.
	 */
	public <W extends WAbstractWidget> W setParent(WLayoutElement parent) {
		this.parent = parent;
		return (W) this;
	}

	@Environment(EnvType.CLIENT)
	public float getWidth() {
		return size.getWidth();
	}

	@Environment(EnvType.CLIENT)
	public float getHeight() {
		return size.getHeight();
	}

	/**
	 * Sets this widget's height.
	 *
	 * @param height Value to be used as height.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setHeight(float height) {
		return setSize(Size.of(size).setHeight(height));
	}

	/**
	 * Sets this widget's width.
	 *
	 * @param width Value to be used as width.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setWidth(float width) {
		return setSize(Size.of(size).setWidth(width));
	}

	/**
	 * Sets this widget's position.
	 *
	 * @param position Value to be used as position.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setPosition(Position position) {
		if (!this.position.equals(position)) {
			this.position = position;
			onLayoutChange();
		}
		return (W) this;
	}

	/**
	 * Method called to center this widget on the horizontal (X) axis, relative to the screen.
	 */
	@Environment(EnvType.CLIENT)
	public void centerX() {
		setPosition(Position.of(getPosition())
				.setX(getParent().getX() + getParent().getWidth() / 2 - getWidth() / 2));
	}

	/**
	 * Method called to center this widget on the vertical (Y) axis, relative to the screen.
	 */
	@Environment(EnvType.CLIENT)
	public void centerY() {
		setPosition(Position.of(getPosition())
				.setY(getParent().getY() + getParent().getHeight() / 2 - getHeight() / 2));
	}

	/**
	 * Overrides a property of this widget's style with a given value.
	 *
	 * @param property Property to be overriden.
	 * @param value    Value for property to be associated with.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W overrideStyle(String property, Object value) {
		styleOverrides.override(property, value);
		return (W) this;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void draw(MatrixStack matrices, VertexConsumerProvider.Immediate provider) {
	}

	// WLayoutElement

	/**
	 * Retrieves this widget's size.
	 *
	 * @return This widget's size.
	 */
	@Environment(EnvType.CLIENT)
	public Size getSize() {
		return size;
	}

	/**
	 * Retrieves this widget's automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's automatic resizing size.
	 */
	@Environment(EnvType.CLIENT)
	public Size getBaseAutoSize() {
		return baseAutoSize;
	}

	/**
	 * Retrieves this widget's minimum automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's minimum automatic resizing size.
	 */
	@Environment(EnvType.CLIENT)
	public Size getMinimumAutoSize() {
		return minimumAutoSize;
	}

	/**
	 * Retrieves this widget's maximum automatic resizing size, used by self-resizing containers.
	 *
	 * @return This widget's maximum automatic resizing size.
	 */
	@Environment(EnvType.CLIENT)
	public Size getMaximumAutoSize() {
		return maximumAutoSize;
	}

	/**
	 * Sets the size of this widget.
	 *
	 * @param size Size this widget should assume.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setSize(Size size) {
		if (!this.size.equals(size)) {
			this.size = size;
			onLayoutChange();
		}
		return (W) this;
	}

	/**
	 * Sets this widget's minimum automatic resizing size, used by self-resizing containers.
	 *
	 * @param minimumAutoSize Minimum automatic resizing size this widget should assume.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setMinimumAutoSize(Size minimumAutoSize) {
		if (!this.minimumAutoSize.equals(minimumAutoSize)) {
			this.minimumAutoSize = minimumAutoSize;
		}
		return (W) this;
	}

	/**
	 * Sets this widget's maximum automatic resizing size, used by self-resizing containers.
	 *
	 * @param maximumAutoSize Maximum automatic resizing size this widget should assume.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setMaximumAutoSize(Size maximumAutoSize) {
		if (!this.maximumAutoSize.equals(maximumAutoSize)) {
			this.maximumAutoSize = maximumAutoSize;
		}
		return (W) this;
	}

	/**
	 * Sets this widget's base/default automatic resizing size, used by self-resizing containers.
	 *
	 * @param baseAutoSize Base/Default automatic resizing size this widget should assume.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setBaseAutoSize(Size baseAutoSize) {
		if (!this.baseAutoSize.equals(baseAutoSize)) {
			this.baseAutoSize = baseAutoSize;

		}
		return (W) this;
	}

	/**
	 * Asserts whether this widget only listens to keyboard events when focused; that is,
	 * when {@link #isFocused()} return true.
	 *
	 * @return True if focused listener; False if not.
	 */
	public boolean isFocusedKeyboardListener() {
		return false;
	}

	/**
	 * Asserts whether this widget only listens to mouse events when focused; that is,
	 * when {@link #isFocused()} return true.
	 *
	 * @return True if focused listener; False if not.
	 */
	public boolean isFocusedMouseListener() {
		return false;
	}

	/**
	 * Dispatches {@link #runnableOnKeyPressed}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyPressed(int keyCode, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyPressed(keyCode, character, keyModifier);
			}
		}
		if (runnableOnKeyPressed != null) {
			runnableOnKeyPressed.event(this, keyCode, character, keyModifier);
		}
	}

	/**
	 * Dispatches {@link #runnableOnKeyReleased}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param keyCode     Keycode associated with pressed key.
	 * @param character   Character associated with pressed key.
	 * @param keyModifier Modifier(s) associated with pressed key.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyReleased(int keyCode, int character, int keyModifier) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onKeyReleased(keyCode, character, keyModifier);
			}
		}
		if (runnableOnKeyReleased != null) {
			runnableOnKeyReleased.event(this, keyCode, character, keyModifier);
		}
	}

	/**
	 * Dispatches {@link #runnableOnCharTyped}, and calls this method
	 * for any children widget event listeners.
	 *
	 * @param character Character associated with key pressed.
	 * @param keyCode   Keycode associated with key pressed.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onCharTyped(char character, int keyCode) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveKeyboard(widget))
					widget.onCharTyped(character, keyCode);
			}
		}
		if (runnableOnCharTyped != null) {
			runnableOnCharTyped.event(this, character, keyCode);
		}
	}

	/**
	 * Dispatches {@link #runnableOnFocusGained}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onFocusGained() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget) && ((WAbstractWidget) widget).isFocused()) {
					widget.onFocusGained();
				}
			}
		}
		if (runnableOnFocusGained != null && isFocused()) {
			runnableOnFocusGained.event(this);
		}
	}

	/**
	 * Dispatches {@link #runnableOnFocusReleased}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onFocusReleased() {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget) && !((WAbstractWidget) widget).isFocused()) {
					widget.onFocusReleased();
				}
			}
		}
		if (runnableOnFocusReleased != null && !isFocused()) {
			runnableOnFocusReleased.event(this);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseReleased}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseReleased(float mouseX, float mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onMouseReleased(mouseX, mouseY, mouseButton);
			}
		}
		if (runnableOnMouseReleased != null) {
			runnableOnMouseReleased.event(this, mouseX, mouseY, mouseButton);
		}

		isHeld = false;
	}

	/**
	 * Dispatches {@link #runnableOnMouseClicked}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseClicked(float mouseX, float mouseY, int mouseButton) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseClicked(mouseX, mouseY, mouseButton);
			}
		}

		if (runnableOnMouseClicked != null) {
			runnableOnMouseClicked.event(this, mouseX, mouseY, mouseButton);
		}

		if (isWithinBounds(mouseX, mouseY)) {
			isHeld = true;
			heldSince = System.currentTimeMillis();
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseDragged}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseDragged(float mouseX, float mouseY, int mouseButton, double deltaX, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget))
					widget.onMouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
			}
		}
		if (runnableOnMouseDragged != null) {
			runnableOnMouseDragged.event(this, mouseX, mouseY, mouseButton, deltaX, deltaY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnMouseMoved}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseMoved(float mouseX, float mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (widget instanceof WAbstractWidget) {
					WAbstractWidget updateWidget = ((WAbstractWidget) widget);
					boolean then = updateWidget.hasFocus;
					updateWidget.updateFocus(mouseX, mouseY);
					boolean now = updateWidget.hasFocus;

					if (then && !now) {
						updateWidget.onFocusReleased();
					} else if (!then && now) {
						updateWidget.onFocusGained();
					}

				}
				if (EventUtilities.canReceiveMouse(widget)) widget.onMouseMoved(mouseX, mouseY);
			}
		}
		if (runnableOnMouseMoved != null) {
			runnableOnMouseMoved.event(this, mouseX, mouseY);
		}
	}

	/**
	 * Method called to update this widget's focus status.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate focus.
	 * @param positionY The vertical (Y) position based on which to calculate focus.
	 * @return True if focused; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean updateFocus(float positionX, float positionY) {
		if (isHidden()) {
			return false;
		}

		setFocus(isWithinBounds(positionX, positionY));
		return isFocused();
	}

	/**
	 * Asserts whether this widget is hidden or not.
	 *
	 * @return True if hidden; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isHidden() {
		if (parent instanceof WAbstractWidget) {
			WAbstractWidget parentWidget = (WAbstractWidget) parent;

			if (parentWidget.isHidden()) {
				return true;
			}
		}
		return isHidden;
	}

	/**
	 * Sets the widget's hidden state.
	 *
	 * @param isHidden Boolean representing true (hidden) or false (visible).
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setHidden(boolean isHidden) {
		this.isHidden = isHidden;
		setFocus(false);
		return (W) this;
	}

	/**
	 * Sets the widget's focus state.
	 *
	 * @param hasFocus Boolean representing true (focused) or false (unfocused).
	 */
	@Environment(EnvType.CLIENT)
	public void setFocus(boolean hasFocus) {
		if (!isFocused() && hasFocus) {
			this.hasFocus = hasFocus;
		}
		if (isFocused() && !hasFocus) {
			this.hasFocus = hasFocus;
		}
	}

	/**
	 * Asserts whether this widget is focused or not.
	 *
	 * @return True if focused; false if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isFocused() {
		return !isHidden() && hasFocus;
	}

	/**
	 * Asserts whether this widget is held or not.
	 *
	 * @return True if held; false if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isHeld() {
		return isHeld;
	}

	/**
	 * Retrieves the time since this widget was last held.
	 *
	 * @return the requested value
	 */
	@Environment(EnvType.CLIENT)
	public long isHeldSince() {
		return heldSince;
	}

	/**
	 * Asserts whether this widget is within boundaries of specified parameters or not.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate boundaries.
	 * @param positionY The vertical (Y) position based on which to calculate boundaries.
	 * @return True if within boundaries; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isWithinBounds(float positionX, float positionY) {
		return isWithinBounds(positionX, positionY, 0);
	}

	/**
	 * Asserts whether this widget is within boundaries of specified parameters or not,
	 * given a vertical and horizontal tolerance.
	 *
	 * @param positionX The horizontal (X) position based on which to calculate boundaries.
	 * @param positionY The vertical (Y) position based on which to calculate boundaries.
	 * @param tolerance The horizontal (X) and vertical (Y) tolerance based on which to calculate boundaries.
	 * @return True if within boundaries; False if not.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isWithinBounds(float positionX, float positionY, float tolerance) {
		return positionX + tolerance > getX()
				&& positionX - tolerance < getWideX()
				&& positionY + tolerance > getY()
				&& positionY - tolerance < getHighY();
	}

	@Environment(EnvType.CLIENT)
	public float getX() {
		return position.getX();
	}

	@Environment(EnvType.CLIENT)
	public float getWideX() {
		return getX() + getWidth();
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetX() {
		return position.getOffsetX();
	}

	@Environment(EnvType.CLIENT)
	public float getY() {
		return position.getY();
	}

	@Environment(EnvType.CLIENT)
	public float getHighY() {
		return getY() + getHeight();
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetY() {
		return position.getOffsetY();
	}

	@Environment(EnvType.CLIENT)
	public float getZ() {
		return position.getZ();
	}

	@Environment(EnvType.CLIENT)
	public float getOffsetZ() {
		return position.getOffsetZ();
	}

	/**
	 * Sets this widget's depth (Z) position.
	 *
	 * @param z Value to be used as depth (Z) position.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setZ(float z) {
		return setPosition(Position.of(position).setZ(z));
	}

	/**
	 * Sets this widget's vertical (Y) position.
	 *
	 * @param y Value to be used as vertical (Y) position.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setY(float y) {
		return setPosition(Position.of(position).setY(y));
	}

	/**
	 * Sets this widget's horizontal (X) position.
	 *
	 * @param x Value to be used as horizontal (X) position.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setX(float x) {
		return setPosition(Position.of(position).setX(x));
	}

	/**
	 * Dispatches {@link #runnableOnMouseScrolled}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseScrolled(float mouseX, float mouseY, double deltaY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				if (EventUtilities.canReceiveMouse(widget)) {
					widget.onMouseScrolled(mouseX, mouseY, deltaY);
				}
			}
		}
		if (runnableOnMouseScrolled != null) {
			runnableOnMouseScrolled.event(this, mouseX, mouseY, deltaY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnDrawTooltip}, and calls this method
	 * for any children widget event listeners.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onDrawTooltip(float mouseX, float mouseY) {
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onDrawTooltip(mouseX, mouseY);
			}
		}
		if (runnableOnDrawTooltip != null) {
			runnableOnDrawTooltip.event(this, mouseX, mouseY);
		}
	}

	/**
	 * Dispatches {@link #runnableOnAlign}, and calls this method
	 * for any children widget event listeners. Also dispatches
	 * a layout change in case positions or sizes changed.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void onAlign() {
		if (runnableOnAlign != null) {
			runnableOnAlign.event(this);
		}
		if (this instanceof WDelegatedEventListener) {
			for (WEventListener widget : ((WDelegatedEventListener) this).getEventDelegates()) {
				widget.onAlign();
			}
		}
		onLayoutChange();
	}

	/**
	 * Retrieves this widget's event called when {@link #onFocusGained()} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WFocusGainListener<W> getOnFocusGained() {
		return runnableOnFocusGained;
	}

	/**
	 * Sets this widget's event called when {@link #onFocusGained()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusGained(WFocusGainListener<W> linkedRunnable) {
		this.runnableOnFocusGained = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onFocusReleased()} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WFocusLossListener<W> getOnFocusReleased() {
		return runnableOnFocusReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onFocusReleased()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnFocusReleased(WFocusLossListener<W> linkedRunnable) {
		this.runnableOnFocusReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onKeyPressed(int, int, int)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WKeyPressListener<W> getOnKeyPressed() {
		return runnableOnKeyPressed;
	}

	/**
	 * Sets this widget's event called when {@link #onKeyPressed(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyPressed(WKeyPressListener<W> linkedRunnable) {
		this.runnableOnKeyPressed = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onCharTyped(char, int)}  is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WCharTypeListener<W> getOnCharTyped() {
		return runnableOnCharTyped;
	}

	/**
	 * Sets this widget's event called when {@link #onCharTyped(char, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnCharTyped(WCharTypeListener<W> linkedRunnable) {
		this.runnableOnCharTyped = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onKeyReleased(int, int, int)}  is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WKeyReleaseListener<W> getOnKeyReleased() {
		return runnableOnKeyReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onKeyReleased(int, int, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnKeyReleased(WKeyReleaseListener<W> linkedRunnable) {
		this.runnableOnKeyReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseClicked(float, float, int)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WMouseClickListener<W> getOnMouseClicked() {
		return runnableOnMouseClicked;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseClicked(float, float, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseClicked(WMouseClickListener<W> linkedRunnable) {
		this.runnableOnMouseClicked = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseDragged(float, float, int, double, double)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WMouseDragListener<W> getOnMouseDragged() {
		return runnableOnMouseDragged;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseDragged(float, float, int, double, double)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseDragged(WMouseDragListener<W> linkedRunnable) {
		this.runnableOnMouseDragged = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseMoved(float, float)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WMouseMoveListener<W> getOnMouseMoved() {
		return runnableOnMouseMoved;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseMoved(float, float)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseMoved(WMouseMoveListener<W> linkedRunnable) {
		this.runnableOnMouseMoved = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseScrolled(float, float, double)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WMouseScrollListener<W> getOnMouseScrolled() {
		return runnableOnMouseScrolled;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseScrolled(float, float, double)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseScrolled(WMouseScrollListener<W> linkedRunnable) {
		this.runnableOnMouseScrolled = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onMouseReleased(float, float, int)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WMouseReleaseListener<W> getOnMouseReleased() {
		return runnableOnMouseReleased;
	}

	/**
	 * Sets this widget's event called when {@link #onMouseReleased(float, float, int)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnMouseReleased(WMouseReleaseListener<W> linkedRunnable) {
		this.runnableOnMouseReleased = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onDrawTooltip(float, float)} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WTooltipDrawListener<W> getOnDrawTooltip() {
		return runnableOnDrawTooltip;
	}

	/**
	 * Sets this widget's event called when {@link #onDrawTooltip(float, float)} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnDrawTooltip(WTooltipDrawListener<W> linkedRunnable) {
		this.runnableOnDrawTooltip = linkedRunnable;
		return (W) this;
	}

	/**
	 * Retrieves this widget's event called when {@link #onAlign()} is called.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> WAlignListener<W> getOnAlign() {
		return runnableOnAlign;
	}

	/**
	 * Sets this widget's event called when {@link #onAlign()} is called.
	 *
	 * @param linkedRunnable Event to be associated with this widget.
	 */
	@Environment(EnvType.CLIENT)
	public <W extends WAbstractWidget> W setOnAlign(WAlignListener<W> linkedRunnable) {
		this.runnableOnAlign = linkedRunnable;
		return (W) this;
	}
}
