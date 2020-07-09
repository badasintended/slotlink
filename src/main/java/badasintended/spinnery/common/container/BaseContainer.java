/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.container;

import badasintended.spinnery.common.registry.NetworkRegistry;
import badasintended.spinnery.common.utility.MutablePair;
import badasintended.spinnery.common.utility.StackUtilities;
import badasintended.spinnery.widget.WAbstractWidget;
import badasintended.spinnery.widget.WInterface;
import badasintended.spinnery.widget.WSlot;
import badasintended.spinnery.widget.api.Action;
import badasintended.spinnery.widget.api.WNetworked;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * A BaseContainer is a class responsible for
 * handling the data needed for the backend of
 * a BaseContainerScreen. It holds data that
 * needs to be synchronized with the server,
 * and is necessary for any widgets that
 * implement WNetworked to be added to it
 * and the BaseContainerScreen.
 * <p>
 * Note, however, that the widget added to
 * the BaseContainer should NOT contain
 * a Position, or a Size.
 */
public class BaseContainer extends ScreenHandler {
	public static final int PLAYER_INVENTORY = 0;
	protected final WInterface serverInterface;
	public Map<Integer, Inventory> inventories = new HashMap<>();
	public Map<Integer, Map<Integer, ItemStack>> cachedInventories = new HashMap<>();
	protected Set<WSlot> splitSlots = new LinkedHashSet<>();
	protected Set<WSlot> singleSlots = new LinkedHashSet<>();
	protected Map<Integer, Map<Integer, ItemStack>> previewStacks = new HashMap<>();
	protected ItemStack previewCursorStack = ItemStack.EMPTY;
	protected World world;

	/**
	 * Instantiates a BaseContainer.
	 *
	 * @param synchronizationID ID to be used for synchronization of this container.
	 * @param playerInventory   PlayerInventory of Player associated with this container.
	 */
	public BaseContainer(int synchronizationID, PlayerInventory playerInventory) {
		super(null, synchronizationID);
		addInventory(PLAYER_INVENTORY, playerInventory);
		setWorld(playerInventory.player.world);
		serverInterface = new WInterface(this);
	}

	/**
	 * Retrieves map of inventories associated with this container.
	 *
	 * @return All inventories associated with this container, whose key is the inventory number.
	 */
	public Map<Integer, Inventory> getInventories() {
		return inventories;
	}

	/**
	 * Sets the World of this container.
	 *
	 * @param world World to be associated with this container.
	 */
	public <C extends BaseContainer> C setWorld(World world) {
		this.world = world;
		return (C) this;
	}

	/**
	 * Gets the preview ItemStack to be rendered instead of the default cursor ItemStack.
	 *
	 * @return previewCursorStack ItemStack to be rendered instead of the default cursor ItemStack.
	 */
	@Environment(EnvType.CLIENT)
	public ItemStack getPreviewCursorStack() {
		return previewCursorStack;
	}

	/**
	 * Sets the preview ItemStack to be rendered instead of the default cursor ItemStack.
	 *
	 * @param previewCursorStack ItemStack to be rendered instead of the default cursor ItemStack.
	 */
	@Environment(EnvType.CLIENT)
	public <C extends BaseContainer> C setPreviewCursorStack(ItemStack previewCursorStack) {
		this.previewCursorStack = previewCursorStack;
		return (C) this;
	}

	/**
	 * Flushes all WSlot drag information.
	 */
	@Environment(EnvType.CLIENT)
	public void flush() {
		getInterface().getContainer().getDragSlots(GLFW.GLFW_MOUSE_BUTTON_1).clear();
		getInterface().getContainer().getDragSlots(GLFW.GLFW_MOUSE_BUTTON_2).clear();
		getInterface().getContainer().getPreviewStacks().clear();
		getInterface().getContainer().setPreviewCursorStack(ItemStack.EMPTY);
	}

	/**
	 * Retrieves the set of WSlots on which the mouse has been dragged, given a mouse button.
	 *
	 * @param mouseButton Mouse button used for dragging.
	 * @return Set of WSlots on which the mouse has been dragged, given the mouse button.
	 */
	@Environment(EnvType.CLIENT)
	public Set<WSlot> getDragSlots(int mouseButton) {
		switch (mouseButton) {
			case 0:
				return splitSlots;
			case 1:
				return singleSlots;
			default:
				return null;
		}
	}

	/**
	 * Retrieves the WInterface attached with this container.
	 *
	 * @return The WInterface attached with this container.
	 */
	public WInterface getInterface() {
		return serverInterface;
	}

	/**
	 * Retrieves the preview ItemStacks associated with all WSlots inventory numbers and numbers.
	 *
	 * @return ItemStacks of all WSlots, whose key is the inventory number, and value key is the number.
	 */
	@Environment(EnvType.CLIENT)
	public Map<Integer, Map<Integer, ItemStack>> getPreviewStacks() {
		return previewStacks;
	}

	/**
	 * Verifies if the mouse is currently being dragged through WSlots.
	 *
	 * @return Whether the mouse is currently being dragged through WSlots.
	 */
	@Environment(EnvType.CLIENT)
	public boolean isDragging() {
		return getDragSlots(GLFW.GLFW_MOUSE_BUTTON_1).isEmpty() || getDragSlots(GLFW.GLFW_MOUSE_BUTTON_2).isEmpty();
	}

	/**
	 * Method called on the server when a WNetworked widget
	 * WNetworked.Event happens.
	 *
	 * @param widgetSyncId Synchronization ID of the WNetworked widget; must match between client and server.
	 * @param event        WNetworked.Event which was sent.
	 * @param payload      CompoundTag payload sent alongside the event.
	 */
	public void onInterfaceEvent(int widgetSyncId, WNetworked.Event event, CompoundTag payload) {
		Set<WAbstractWidget> checkWidgets = serverInterface.getAllWidgets();
		for (WAbstractWidget widget : checkWidgets) {
			if (!(widget instanceof WNetworked)) continue;
			if (((WNetworked) widget).getSyncId() == widgetSyncId) {
				((WNetworked) widget).onInterfaceEvent(event, payload);
				return;
			}
		}
	}

	/**
	 * Method called when a drag Action is performed on a WSlot, both on the client and server.
	 *
	 * @param slotNumber      Number of WSlot in which the Action happened.
	 * @param inventoryNumber Inventory number of WSlot in which the Action happened.
	 * @param action          Action which was performed.
	 */
	public void onSlotDrag(int[] slotNumber, int[] inventoryNumber, Action action) {
		Set<WSlot> slots = new LinkedHashSet<>();

		for (int i = 0; i < slotNumber.length; ++i) {
			WSlot slot = getInterface().getSlot(inventoryNumber[i], slotNumber[i]);

			if (slot != null) {
				slots.add(slot);
			}
		}

		if (slots.isEmpty()) {
			return;
		}

		int split;

		if (action.isSplit()) {
			split = Math.max(getPlayerInventory().getCursorStack().getCount() / slots.size(), 1);
		} else {
			split = 1;
		}

		ItemStack stackA;

		if (action.isPreview()) {
			stackA = getPlayerInventory().getCursorStack().copy();
		} else {
			stackA = getPlayerInventory().getCursorStack();
		}

		if (stackA.isEmpty()) {
			return;
		}

		for (WSlot slotA : slots) {
			if (slotA.refuses(stackA)) continue;

			ItemStack stackB;
			if (action.isPreview()) {
				stackB = slotA.getStack().copy();
			} else {
				stackB = slotA.getStack();
			}

			MutablePair<ItemStack, ItemStack> stacks = StackUtilities.merge(stackA, stackB, split, Math.min(stackA.getMaxCount(), (split + stackB.getCount())));

			if (action.isPreview()) {
				this.previewCursorStack = stacks.getFirst().copy();
				slotA.setPreviewStack(stacks.getSecond().copy());
			} else {
				stackA = stacks.getFirst();
				this.previewCursorStack = ItemStack.EMPTY;
				slotA.setStack(stacks.getSecond());
			}
		}
	}

	/**
	 * Method called when an Action is performed on a WSlot, both on the client and server.
	 *
	 * @param slotNumber      Number of WSlot in which the Action happened.
	 * @param inventoryNumber Inventory number of WSlot in which the Action happened.
	 * @param button          Button with which the action was performed.
	 * @param action          Action which was performed.
	 * @param player          Player whom performed the action.
	 *                        <p>
	 *                        As a warning, it just works. Any modifications are likely to cause
	 *                        severe brain damage to the poor individual attempting to change this.
	 */
	public void onSlotAction(int slotNumber, int inventoryNumber, int button, Action action, PlayerEntity player) {
		WSlot slotA = getInterface().getSlot(inventoryNumber, slotNumber);

		if (slotA == null || slotA.isLocked()) {
			return;
		}

		ItemStack stackA = slotA.getStack().copy();
		ItemStack stackB = player.inventory.getCursorStack().copy();

		PlayerInventory inventory = getPlayerInventory();

		switch (action) {
			case PICKUP: {

				if (!StackUtilities.equalItemAndTag(stackA, stackB)) {
					if (button == 0) { // Interact with existing // LMB
						if (slotA.isOverrideMaximumCount()) {
							if (stackA.isEmpty()) {
								if (slotA.refuses(stackB)) return;

								slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK);
								StackUtilities.merge(stackB, stackA, stackB.getMaxCount(), slotA.getMaxCount()).apply(inventory::setCursorStack, slotA::acceptStack);
							} else if (stackB.isEmpty()) {
								if (slotA.refuses(stackB)) return;

								slotA.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK);
								StackUtilities.merge(stackA, stackB, slotA.getInventoryNumber() == PLAYER_INVENTORY ? stackB.getMaxCount() : slotA.getMaxCount(), stackB.getMaxCount()).apply(slotA::acceptStack, inventory::setCursorStack);
							}
						} else {
							if (!stackB.isEmpty() && slotA.refuses(stackB)) return;

							slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_DEFAULT_FULL_STACK);

							if (!StackUtilities.equalItemAndTag(stackA, stackB)) {
								slotA.setStack(stackB);
								player.inventory.setCursorStack(stackA);
							} else {
								StackUtilities.merge(stackA, stackB, stackA.isEmpty() || slotA.getInventoryNumber() == PLAYER_INVENTORY ? stackB.getMaxCount() : slotA.getMaxCount(), stackB.getMaxCount()).apply(slotA::acceptStack, inventory::setCursorStack);
							}
						}
					} else if (button == 1 && !stackB.isEmpty()) { // Interact with existing // RMB
						slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM);
						StackUtilities.merge(inventory::getCursorStack, slotA::getStack, inventory.getCursorStack()::getMaxCount, () -> (slotA.getStack().getCount() == slotA.getMaxCount() ? 0 : slotA.getStack().getCount() + 1)).apply(inventory::setCursorStack, slotA::setStack);
					} else if (button == 1) { // Split existing // RMB
						slotA.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_DEFAULT_HALF_STACK);
						StackUtilities.merge(slotA::getStack, inventory::getCursorStack, inventory.getCursorStack()::getMaxCount, () -> Math.max(1, Math.min(slotA.getStack().getMaxCount() / 2, slotA.getStack().getCount() / 2))).apply(slotA::setStack, inventory::setCursorStack);
					}
				} else {
					if (button == 0) {
						if (slotA.refuses(stackB)) return;

						slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_FULL_STACK);
						StackUtilities.merge(inventory::getCursorStack, slotA::getStack, stackB::getMaxCount, slotA::getMaxCount).apply(inventory::setCursorStack, slotA::setStack); // Add to existing // LMB
					} else {
						if (slotA.refuses(stackB)) return;

						slotA.consume(action, Action.Subtype.FROM_CURSOR_TO_SLOT_CUSTOM_SINGLE_ITEM);
						StackUtilities.merge(inventory::getCursorStack, slotA::getStack, inventory.getCursorStack()::getMaxCount, () -> (slotA.getStack().getCount() == slotA.getMaxCount() ? 0 : slotA.getStack().getCount() + 1)).apply(inventory::setCursorStack, slotA::setStack); // Add to existing // RMB
					}
				}
				break;
			}
			case CLONE: {
				if (player.isCreative()) {
					stackB = new ItemStack(stackA.getItem(), stackA.getMaxCount()); // Clone existing // MMB
					stackB.setTag(stackA.getTag());
					inventory.setCursorStack(stackB);
				}
				break;
			}
			case QUICK_MOVE: {
				for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
					if (widget instanceof WSlot && ((WSlot) widget).getLinkedInventory() != slotA.getLinkedInventory()) {
						WSlot slotB = ((WSlot) widget);
						ItemStack stackC = slotB.getStack();
						stackA = slotA.getStack();

						if (slotB.refuses(stackA)) continue;
						if (slotB.isLocked()) continue;

						if ((!slotA.getStack().isEmpty() && stackC.isEmpty()) || (StackUtilities.equalItemAndTag(stackA, stackC) && stackC.getCount() < (slotB.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : slotB.getMaxCount()))) {
							int maxB = stackC.isEmpty() || slotB.getInventoryNumber() == PLAYER_INVENTORY ? stackA.getMaxCount() : slotB.getMaxCount();
							slotA.consume(action, Action.Subtype.FROM_SLOT_TO_SLOT_CUSTOM_FULL_STACK);
							StackUtilities.merge(slotA::getStack, slotB::getStack, slotA::getMaxCount, () -> maxB).apply(slotA::setStack, slotB::setStack);
							break;
						}
					}
				}
				break;
			}
			case PICKUP_ALL: {
				for (WAbstractWidget widget : getInterface().getAllWidgets()) {
					if (widget instanceof WSlot && StackUtilities.equalItemAndTag(((WSlot) widget).getStack(), stackB)) {
						WSlot slotB = (WSlot) widget;

						if (slotB.isLocked()) continue;

						slotB.consume(action, Action.Subtype.FROM_SLOT_TO_CURSOR_CUSTOM_FULL_STACK);
						StackUtilities.merge(slotB::getStack, inventory::getCursorStack, slotB::getMaxCount, stackB::getMaxCount).apply(slotB::setStack, inventory::setCursorStack);
					}
				}
			}
		}
	}

	/**
	 * Retrieves the World associated with this container.
	 *
	 * @return World associated with this container.
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Retrieves the PlayerInventory of the Player associated with this container.
	 *
	 * @return PlayerInventory of player associated with this container.
	 */
	public PlayerInventory getPlayerInventory() {
		return (PlayerInventory) inventories.get(PLAYER_INVENTORY);
	}

	/**
	 * Retrieves an inventory from the BaseContainer.
	 *
	 * @param inventoryNumber Inventory number associated with the inventory.
	 * @return Inventory associated with the inventory number.
	 */
	public Inventory getInventory(int inventoryNumber) {
		return inventories.get(inventoryNumber);
	}

	/**
	 * Adds an inventory to the BaseContainer.
	 *
	 * @param inventoryNumber Inventory number associated with the inventory.
	 * @param inventory       Inventory associated with the inventory number.
	 */
	public <C extends BaseContainer> C addInventory(int inventoryNumber, Inventory inventory) {
		this.inventories.put(inventoryNumber, inventory);
		return (C) this;
	}

	/**
	 * Dispatches packets for WSlots whose contents
	 * have changed since the last call.
	 */
	@Override
	public void sendContentUpdates() {
		if (!(this.getPlayerInventory().player instanceof ServerPlayerEntity))
			return;

		for (WAbstractWidget widget : serverInterface.getAllWidgets()) {
			if (widget instanceof WSlot) {
				WSlot slotA = ((WSlot) widget);


				if (cachedInventories.get(slotA.getInventoryNumber()) != null && cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber()) != null) {
					ItemStack stackA = slotA.getStack();
					ItemStack stackB = cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber());

					if (stackA.getItem() != stackB.getItem() || stackA.getCount() != stackB.getCount() || (stackA.hasTag() && !stackA.getTag().equals(stackB.getOrCreateTag()))) {
						ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.getPlayerInventory().player, NetworkRegistry.SLOT_UPDATE_PACKET, NetworkRegistry.createSlotUpdatePacket(syncId, slotA.getSlotNumber(), slotA.getInventoryNumber(), slotA.getStack()));
					}

					cachedInventories.get(slotA.getInventoryNumber()).put(slotA.getSlotNumber(), slotA.getStack().copy());
				} else {
					cachedInventories.computeIfAbsent(slotA.getInventoryNumber(), value -> new HashMap<>());

					ItemStack stackA = slotA.getStack();
					ItemStack stackB = Optional.ofNullable(cachedInventories.get(slotA.getInventoryNumber()).get(slotA.getSlotNumber())).orElse(ItemStack.EMPTY);

					if (stackA.getItem() != stackB.getItem() || stackA.getCount() != stackB.getCount() || (stackA.hasTag() && !stackA.getTag().equals(stackB.getOrCreateTag()))) {
						ServerSidePacketRegistry.INSTANCE.sendToPlayer(this.getPlayerInventory().player, NetworkRegistry.SLOT_UPDATE_PACKET, NetworkRegistry.createSlotUpdatePacket(syncId, slotA.getSlotNumber(), slotA.getInventoryNumber(), slotA.getStack()));
					}

					cachedInventories.get(slotA.getInventoryNumber()).put(slotA.getSlotNumber(), slotA.getStack().copy());
				}
			}
		}
	}

	/**
	 * Method deprecated and unsupported by Spinnery.
	 */
	@Deprecated
	@Override
	public Slot addSlot(Slot slot) {
		throw new UnsupportedOperationException(Slot.class.getName() + " cannot be added to a Spinnery " + BaseContainer.class.getName() + "!");
	}

	/**
	 * Method deprecated and unsupported by Spinnery.
	 */
	@Deprecated
	@Override
	public ItemStack onSlotClick(int identifier, int button, SlotActionType action, PlayerEntity player) {
		return ItemStack.EMPTY;
	}

	/**
	 * Return true by default for simplicity of use.
	 */
	@Override
	public boolean canUse(PlayerEntity entity) {
		return true;
	}
}
