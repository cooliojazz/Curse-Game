package com.up.cursegame.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/**
 *
 * @author Ricky
 */
public class PlayerDataCapabilityProvider implements ICapabilitySerializable<INBT> {
	
	@CapabilityInject(PlayerData.class)
	public static Capability<PlayerData> PLAYER_DATA_CAPABILITY = null;
	
    private LazyOptional<PlayerData> instance;

	public PlayerDataCapabilityProvider() {
//		instance = LazyOptional.of(PLAYER_DATA_CAPABILITY::getDefaultInstance);
		instance = LazyOptional.of(() -> new PlayerData());
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cpblt, Direction down) {
		return cpblt == PLAYER_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
	}

	@Override
	public INBT serializeNBT() {
		return PLAYER_DATA_CAPABILITY.writeNBT(instance.orElse(null), null);
	}

	@Override
	public void deserializeNBT(final INBT nbt) {
		PLAYER_DATA_CAPABILITY.readNBT(instance.orElse(null), null, nbt);
	}
}
