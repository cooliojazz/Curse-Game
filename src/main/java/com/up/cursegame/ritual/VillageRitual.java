package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author Ricky
 */
public class VillageRitual extends RitualGenerator {
	
	private static ItemPool pool = new ItemPool(Arrays.asList(
			withCount(Items.EMERALD.getDefaultInstance(), 16),
			withCount(Items.EMERALD.getDefaultInstance(), 32),
			Items.CHAINMAIL_LEGGINGS.getDefaultInstance(),
			Items.CHAINMAIL_CHESTPLATE.getDefaultInstance(),
			withCount(Items.NAME_TAG.getDefaultInstance(), 2),
			withCount(Items.BELL.getDefaultInstance(), 4),
			withCount(Items.BELL.getDefaultInstance(), 8),
			withCount(Items.RABBIT_STEW.getDefaultInstance(), 1),
//			withCount(Items.RABBIT_STEW.getDefaultInstance(), 16),
//			withCount(Items.RABBIT_STEW.getDefaultInstance(), 32),
			withCount(Items.HAY_BLOCK.getDefaultInstance(), 64)));
	
	@Override
	public Set<ItemStack> generateRequirements() {
		return pool.generateItems(3);
	}

	@Override
	public int generateCureAmount(int players) {
		return randomRange((int)Math.round(players * 0.25), (int)Math.round(players * 0.5));
	}

	private static final String[] descriptors = {"Friendly", "Forceful", "Holy", "Bountiful", "Superb", "Painful"};
	
	@Override
	protected String generateName() {
		return descriptors[(int)(Math.random() * descriptors.length)] + " Village Ceremony";
	}
	
}
