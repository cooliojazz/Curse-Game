package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;

/**
 *
 * @author Ricky
 */
public class BastionRitual extends RitualGenerator {
	
	private static ItemPool pool = new ItemPool(Arrays.asList(
			withCount(Items.GOLD_BLOCK.getDefaultInstance(), 16),
			withCount(Items.CRYING_OBSIDIAN.getDefaultInstance(), 16),
			withCount(Items.NETHERITE_INGOT.getDefaultInstance(), 4),
			withCount(Items.DIAMOND.getDefaultInstance(), 16),
			withCount(Items.ENDER_EYE.getDefaultInstance(), 32),
			withCount(PotionUtils.setPotion(Items.POTION.getDefaultInstance(), Potions.FIRE_RESISTANCE), 16),
			withCount(Items.ENCHANTED_GOLDEN_APPLE.getDefaultInstance(), 1),
			withCount(Items.MAGMA_BLOCK.getDefaultInstance(), 64),
			withCount(Items.MAGMA_CREAM.getDefaultInstance(), 32)));
	
	@Override
	public Set<ItemStack> generateRequirements() {
		return pool.generateItems(3);
	}

	@Override
	public int generateCureAmount(int players) {
		return randomRange((int)Math.round(players * 0.5), (int)Math.round(players * 0.75));
	}

	private static final String[] descriptors = {"Crazy", "Forceful", "Spicy", "Wealthy", "Porcine", "Infernal"};
	
	@Override
	protected String generateName() {
		return descriptors[(int)(Math.random() * descriptors.length)] + " Bastion Cleansing";
	}
	
}
