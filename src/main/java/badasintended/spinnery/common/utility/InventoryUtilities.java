/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.utility;

import badasintended.spinnery.Spinnery;
import badasintended.spinnery.common.inventory.BaseInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.apache.logging.log4j.Level;

public class InventoryUtilities {
	/**
	 * Write inventory contents to a CompoundTag with support for ItemStacks greater than 64 in size.
	 *
	 * @param inventory Inventory CompoundTag will be written from.
	 * @return Tag from inventory.
	 */
	@Deprecated
	public static <T extends Inventory> CompoundTag writeUnsafe(T inventory, CompoundTag tag) {
		for (int i = 0; i < inventory.size(); ++i) {
			tag.put(String.valueOf(i), inventory.getStack(i).toTag(new CompoundTag()));
		}

		return tag;
	}

	/**
	 * Read inventory contents from a CompoundTag with support for ItemStacks greater than 64 in size.
	 *
	 * @param tag Tag Inventory will be read from.
	 * @return Inventory from tag.
	 */
	public static <T extends Inventory> T readUnsafe(T inventory, CompoundTag tag) {
		for (int i = 0; i < inventory.size(); ++i) {
			try {
				inventory.setStack(i, ItemStack.fromTag((CompoundTag) tag.get(String.valueOf(i))));
			} catch (IndexOutOfBoundsException exception) {
				Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be written: inventory size smaller than necessary!");
				return inventory;
			}
		}

		return inventory;
	}

	/**
	 * Write inventory contents to a CompoundTag with support for ItemStacks greater than 64 in size.
	 */
	public static CompoundTag write(Inventory inventory) {
		return write(inventory, null);
	}

	/**
	 * Write inventory contents to a CompoundTag with support for ItemStacks greater than 64 in size.
	 */
	public static CompoundTag write(Inventory inventory, CompoundTag tag) {
		if (inventory == null || inventory.size() <= 0) return StackUtilities.TAG_EMPTY;

		if (tag == null) tag = new CompoundTag();

		CompoundTag inventoryTag = new CompoundTag();

		CompoundTag stacksTag = new CompoundTag();

		inventoryTag.putInt("size", inventory.size());

		for (int position = 0; position < inventory.size(); ++position) {
			if (inventory.getStack(position) != null && inventory.getStack(position) != ItemStack.EMPTY) {
				ItemStack stack = inventory.getStack(position);

				if (stack != null && !stack.isEmpty()) {
					CompoundTag stackTag = inventory.getStack(position).toTag(new CompoundTag());

					if (stackTag != StackUtilities.TAG_EMPTY) {
						stacksTag.put(String.valueOf(position), stackTag);
					}
				}
			}
		}

		inventoryTag.put("stacks", stacksTag);

		tag.put("inventory", inventoryTag);

		return tag;
	}

	/**
	 * Read inventory contents from a CompoundTag with support for ItemStacks greater than 64 in size.
	 */
	public static <T extends BaseInventory> T read(CompoundTag tag) {
		if (tag == null) return null;

		if (!tag.contains("inventory")) {
			Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be read: " + CompoundTag.class.getName() + " does not contain 'inventory' subtag!");
			return null;
		} else {
			Tag rawTag = tag.get("inventory");

			if (!(rawTag instanceof CompoundTag)) {
				Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be read: " + rawTag.getClass().getName() + " is not instance of " + CompoundTag.class.getName() + "!");
				return null;
			} else {
				CompoundTag compoundTag = (CompoundTag) rawTag;

				if (!compoundTag.contains("size")) {
					Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be read: " + CompoundTag.class.getName() + " does not contain 'size' value!");
					return null;
				} else {
					int size = compoundTag.getInt("size");

					if (size == 0)
						Spinnery.LOGGER.log(Level.WARN, "[Spinnery] Inventory contents size successfully read, but with size of zero. This may indicate a non-integer 'size' value!");

					if (!compoundTag.contains("stacks")) {
						Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be read: " + CompoundTag.class.getName() + " does not contain 'stacks' subtag!");
						return null;
					} else {
						Tag rawStacksTag = compoundTag.get("stacks");

						if (!(rawStacksTag instanceof CompoundTag)) {
							Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory contents failed to be read: " + rawStacksTag.getClass().getName() + " is not instance of " + CompoundTag.class.getName() + "!");
							return null;
						} else {
							CompoundTag stacksTag = (CompoundTag) rawStacksTag;

							BaseInventory inventory = new BaseInventory(size);

							for (int position = 0; position < size; ++position) {
								if (stacksTag.contains(String.valueOf(position))) {
									Tag rawStackTag = stacksTag.get(String.valueOf(position));

									if (!(rawStackTag instanceof CompoundTag)) {
										Spinnery.LOGGER.log(Level.ERROR, "[Spinnery] Inventory stack skipped: stored tag not instance of " + CompoundTag.class.getName() + "!");
										return null;
									} else {
										CompoundTag stackTag = (CompoundTag) rawStackTag;

										ItemStack stack = ItemStack.fromTag(stackTag);

										if (stack == ItemStack.EMPTY) {
											Spinnery.LOGGER.log(Level.WARN, "[Spinnery] Inventory stack skipped: stack was empty!");
										} else {
											inventory.setStack(position, stack);
										}
									}
								}
							}

							return (T) inventory;
						}
					}
				}
			}
		}
	}
}
