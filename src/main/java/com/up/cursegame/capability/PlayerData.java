package com.up.cursegame.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 *
 * @author Ricky
 */
public class PlayerData {
	
	private int prevLives = -1;
	private int lives = 0;
	private boolean cursed = true;
	private boolean active = false;

	public int getPrevLives() {
		return prevLives;
	}

	public void setPrevLives(int prevLives) {
		this.prevLives = prevLives;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public boolean isCursed() {
		return cursed;
	}

	public void setCursed(boolean cursed) {
		this.cursed = cursed;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public static void register() {
		CapabilityManager.INSTANCE.register(PlayerData.class, storage, PlayerData::new);
	}

	public static Storage storage = new Storage();
	public static class Storage implements Capability.IStorage<PlayerData> {

		@Override
		public INBT writeNBT(Capability<PlayerData> capability, PlayerData instance, Direction side) {
			System.out.println("Writing Player NBT");
			CompoundNBT root = new CompoundNBT();
			root.putInt("prevLives", instance.prevLives);
			root.putInt("lives", instance.lives);
			root.putBoolean("cursed", instance.cursed);
			root.putBoolean("active", instance.active);
			return root;
		}

		@Override
		public void readNBT(Capability<PlayerData> capability, PlayerData instance, Direction side, INBT nbt) {
			System.out.println("Reading Player NBT");
			CompoundNBT root = (CompoundNBT)nbt;
			instance.prevLives = root.getInt("prevLives");
			instance.lives = root.getInt("lives");
			instance.cursed = root.getBoolean("cursed");
			instance.active = root.getBoolean("active");
		}
	}
	
}
