package com.up.cursegame.ritual;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

/**
 *
 * @author Ricky
 */
public class Ritual {
	
	private String name;
	private Set<ItemStack> requirements;
	private Set<ItemStack> completions;
	private int cureAmount;

	public Ritual(String name, Set<ItemStack> requirements, Set<ItemStack> completed, int cureAmount) {
		this.name = name;
		this.requirements = requirements;
		this.completions = completed;
		this.cureAmount = cureAmount;
	}

	public Ritual(String name, Set<ItemStack> requirements, int cureAmount) {
		this(name, requirements, new HashSet<ItemStack>(), cureAmount);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ItemStack> getRequirements() {
		return requirements;
	}

	public void setRequirements(Set<ItemStack> requirements) {
		this.requirements = requirements;
	}

	public Set<ItemStack> getCompletions() {
		return completions;
	}

	public void setCompletions(Set<ItemStack> completions) {
		this.completions = completions;
	}

	public int getCureAmount() {
		return cureAmount;
	}

	public void setCureAmount(int cureAmount) {
		this.cureAmount = cureAmount;
	}

	public Set<ItemStack> getUncompleted() {
		return requirements.stream().filter(r -> !completions.contains(r)).collect(Collectors.toSet());
	}
	
	public boolean isComplete() {
		return requirements.stream().allMatch(r -> completions.stream().anyMatch(c -> r.sameItemStackIgnoreDurability(c)));
	}
	
	public boolean isRequiredItem(Item i) {
		return requirements.stream().anyMatch(r -> r.getItem() == i);
	}
	
	public ItemStack getRequirement(Item i) {
		return requirements.stream().filter(r -> r.getItem() == i).findFirst().orElse(null);
	}
	
	public ItemStack getCompletion(Item i) {
		return completions.stream().filter(r -> r.getItem() == i).findFirst().orElse(null);
	}
	
	public int getRemaining(ItemStack requirement) {
		ItemStack completion = getCompletion(requirement.getItem());
		return requirement.getCount() - (completion == null ? 0 : completion.getCount());
	}
	
	public int getRemaining(Item i) {
		return getRemaining(getRequirement(i));
	}
	
	public void complete(ItemStack is) {
		ItemStack completion = getCompletion(is.getItem());
		if (completion == null) {
			completion = new ItemStack(is.getItem(), 0);
			completions.add(completion);
		}
		completion.setCount(completion.getCount() + is.getCount());
	}
	
	public CompoundNBT toTag() {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("name", name);
		ListNBT rList = new ListNBT();
		requirements.forEach(is -> rList.add(is.serializeNBT()));
		tag.put("requirements", rList);
		ListNBT cList = new ListNBT();
		completions.forEach(is -> cList.add(is.serializeNBT()));
		tag.put("completed", cList);
		tag.putInt("cureAmount", cureAmount);
		return tag;
	}
	
	public static Ritual fromTag(CompoundNBT tag) {
		HashSet<ItemStack> rItems = new HashSet<>();
		((ListNBT)tag.get("requirements")).forEach(ct -> rItems.add(ItemStack.of((CompoundNBT)ct)));
		HashSet<ItemStack> cItems = new HashSet<>();
		((ListNBT)tag.get("completed")).forEach(ct -> cItems.add(ItemStack.of((CompoundNBT)ct)));
		return new Ritual(tag.getString("name"), rItems, cItems, tag.getInt("cureAmount"));
	}
}
