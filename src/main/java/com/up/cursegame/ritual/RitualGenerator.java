package com.up.cursegame.ritual;

import java.util.Set;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Ricky
 */
public abstract class RitualGenerator {
	
	abstract protected Set<ItemStack> generateRequirements();
	abstract protected int generateCureAmount(int players);
	abstract protected String generateName();
	
	public Ritual generate(int players) {
		return new Ritual(generateName(), generateRequirements(), generateCureAmount(players));
	}
	
	protected static ItemStack withCount(ItemStack stack, int count) {
		stack.setCount(count);
		return stack;
	}
	
	/**
	 * Lower and upper are both inclusive
	 * @param lower
	 * @param upper
	 * @return 
	 */
	protected static int randomRange(int lower, int upper) {
		return lower + (int)(Math.random() * (upper + 1 - lower));
	}
}
