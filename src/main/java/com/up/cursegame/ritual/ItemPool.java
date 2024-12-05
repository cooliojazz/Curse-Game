package com.up.cursegame.ritual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Ricky
 */
public class ItemPool {
	
	private List<ItemStack> pool;

	public ItemPool() {
		this.pool = new ArrayList<>();
	}

	public ItemPool(Collection<ItemStack> pool) {
		this.pool = new ArrayList<>(pool);
	}

	public List<ItemStack> getPool() {
		return pool;
	}
	
	public Set<ItemStack> generateItems(int count) {
		HashSet<ItemStack> items = new HashSet<>();
		while (items.size() < count) {
			items.add(pool.get((int)(Math.random() * pool.size())));
		}
		return items;
	}
	
}
