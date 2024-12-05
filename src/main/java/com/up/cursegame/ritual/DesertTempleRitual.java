package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author Ricky
 */
public class DesertTempleRitual extends RitualGenerator {
	
	private static ItemPool pool = new ItemPool(Arrays.asList(
			withCount(Items.CACTUS.getDefaultInstance(), 64),
			withCount(Items.DEAD_BUSH.getDefaultInstance(), 64),
			withCount(Items.ROTTEN_FLESH.getDefaultInstance(), 32),
			withCount(Items.RABBIT_HIDE.getDefaultInstance(), 16),
			withCount(Items.RABBIT_FOOT.getDefaultInstance(), 8)));
	
	@Override
	public Set<ItemStack> generateRequirements() {
		return pool.generateItems(2);
	}

	@Override
	public int generateCureAmount(int players) {
		return randomRange(1, 2);
	}

	private static final String[] descriptors = {"Cleansing", "Freeing", "Holy", "Binding", "Mega", "Useless"};
	
	@Override
	protected String generateName() {
		return descriptors[(int)(Math.random() * descriptors.length)] + " Desert Ritual";
	}
	
}
