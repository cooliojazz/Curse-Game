package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author Ricky
 */
public class OceanMonumentRitual extends RitualGenerator {
	
	private static ItemPool pool = new ItemPool(Arrays.asList(
			withCount(Items.SPONGE.getDefaultInstance(), 16),
			withCount(Items.PRISMARINE_SHARD.getDefaultInstance(), 16),
			withCount(Items.PRISMARINE_CRYSTALS.getDefaultInstance(), 8),
			withCount(Items.SEA_LANTERN.getDefaultInstance(), 8),
			withCount(Items.DARK_PRISMARINE.getDefaultInstance(), 8)));
	
	@Override
	public Set<ItemStack> generateRequirements() {
		return pool.generateItems(3);
	}

	@Override
	public int generateCureAmount(int players) {
		return randomRange((int)Math.round(players * 0.33), (int)Math.round(players * 0.67));
	}

	private static final String[] descriptors = {"Drowned", "Bubbly", "Binding", "Glorious", "Mundane", "Painful"};
	
	@Override
	protected String generateName() {
		return descriptors[(int)(Math.random() * descriptors.length)] + " Ocean Consecration";
	}
	
}
