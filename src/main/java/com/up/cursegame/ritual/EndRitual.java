package com.up.cursegame.ritual;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/**
 *
 * @author Ricky
 */
public class EndRitual extends RitualGenerator {
	
	@Override
	public Set<ItemStack> generateRequirements() {
		ItemStack crystal = Items.END_CRYSTAL.getDefaultInstance();
		crystal.setCount(4);
		return new HashSet<>(Arrays.asList(crystal));
	}

	@Override
	public int generateCureAmount(int players) {
		return players;
	}
	
	@Override
	protected String generateName() {
		return "Final Ritual";
	}
	
}
