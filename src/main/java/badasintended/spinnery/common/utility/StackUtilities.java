/*
 * This code is taken from Spinnery project, with some modification
 * allowing slotlink mod to use old feature that broken on newer version.
 *
 * Spinnery is licensed under LGPLv3.0.
 * (https://github.com/vini2003/Spinnery)
 */

package badasintended.spinnery.common.utility;

import badasintended.spinnery.common.container.BaseContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StackUtilities {
	public static final CompoundTag TAG_EMPTY = new CompoundTag();

	/**
	 * Support merging stacks with customized maximum count.
	 * You may be wondering why use Suppliers. I ask you,
	 * instead, why not?
	 * In fact, it is the best idea I have had up to this
	 * very day.
	 * What {@link BaseContainer} does,
	 * is chain a {@link MutablePair#apply(Consumer, Consumer)},
	 * right afterwards, which applies the ItemStack to
	 * whichever method the Consumer is linked to. In that
	 * case, a method to set something's ItemStack.
	 * I hope you thoroughly enjoy this addition. If any
	 * WSlot code breaks, it's either my fault, or my fault.
	 *
	 * @param supplierA Source ItemStack supplier
	 * @param supplierB Destination ItemStack supplier
	 * @param sA        Max. count of stackA supplier
	 * @param sB        Max. count of stackB supplier
	 * @return Resulting ItemStacks
	 */
	public static MutablePair<ItemStack, ItemStack> merge(Supplier<ItemStack> supplierA, Supplier<ItemStack> supplierB, Supplier<Integer> sA, Supplier<Integer> sB) {
		return merge(supplierA.get(), supplierB.get(), sA.get(), sB.get());
	}

	/**
	 * Support merging stacks with customized maximum count.
	 *
	 * @param stackA Source ItemStack
	 * @param stackB Destination ItemStack
	 * @param maxA   Max. count of stackA
	 * @param maxB   Max. count of stackB
	 * @return Resulting ItemStacks
	 */
	public static MutablePair<ItemStack, ItemStack> merge(ItemStack stackA, ItemStack stackB, int maxA, int maxB) {
		if (equalItemAndTag(stackA, stackB)) {
			int countA = stackA.getCount();
			int countB = stackB.getCount();

			int availableA = Math.max(0, maxA - countA);
			int availableB = Math.max(0, maxB - countB);

			stackB.increment(Math.min(countA, availableB));
			stackA.setCount(Math.max(countA - availableB, 0));
		} else {
			if (stackA.isEmpty() && !stackB.isEmpty()) {
				int countA = stackA.getCount();
				int availableA = maxA - countA;

				int countB = stackB.getCount();

				stackA = stackB.copy();
				stackA.setCount(Math.min(countB, availableA));

				stackA.setTag(stackB.getTag());
				stackB.decrement(Math.min(countB, availableA));
			} else if (stackB.isEmpty() && !stackA.isEmpty()) {
				int countB = stackB.getCount();
				int availableB = maxB - countB;

				int countA = stackA.getCount();

				stackB = stackA.copy();
				stackB.setCount(Math.min(countA, availableB));
				stackB.setTag(stackA.getTag());
				stackA.decrement(Math.min(countA, availableB));
			}
		}

		return MutablePair.of(stackA, stackB);
	}

	/**
	 * Asserts whether two ItemStacks are equal in Item and Tag.
	 *
	 * @param stackA Stack one.
	 * @param stackB Stack two.
	 * @return True if one and two match in Item and Tag; False if not.
	 */
	public static boolean equalItemAndTag(ItemStack stackA, ItemStack stackB) {
		return ItemStack.areItemsEqual(stackA, stackB) && stackA.getTag() == stackB.getTag();
	}
}
