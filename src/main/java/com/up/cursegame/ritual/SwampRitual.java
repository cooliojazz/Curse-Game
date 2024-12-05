package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author Ricky
 */
public class SwampRitual extends RitualGenerator {
	
	private static ItemPool pool = new ItemPool(Arrays.asList(
			withCount(Items.VINE.getDefaultInstance(), 64),
			withCount(Items.SLIME_BALL.getDefaultInstance(), 64),
			withCount(Items.SLIME_BLOCK.getDefaultInstance(), 16),
			withCount(Items.REDSTONE.getDefaultInstance(), 32),
			withCount(Items.GLASS_BOTTLE.getDefaultInstance(), 16),
			withCount(Items.POTION.getDefaultInstance(), 16)));
	
	@Override
	public Set<ItemStack> generateRequirements() {
		return pool.generateItems(2);
	}

	@Override
	public int generateCureAmount(int players) {
		return randomRange(1, 2);
	}

	private static final String[] descriptors = {"Magical", "Spooky", "Holy", "Dark", "Superb", "Grand"};
	
	@Override
	protected String generateName() {
		return descriptors[(int)(Math.random() * descriptors.length)] + " Swamp Ceremony";
	}
	
}
