package com.up.cursegame.util;

import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.network.PlayerDataPacket;
import com.up.cursegame.network.PacketHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 *
 * @author Ricky
 */
public class DataUtil {
	
	public static PlayerData getData(PlayerEntity player) {
		return player.getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
	}
	
	public static void updateData(ServerPlayerEntity player) {
//		PacketHandlers.sendAll(player.getServer(), new PlayerDataPacket(getData(player)));
		PacketHandlers.send(player, new PlayerDataPacket(getData(player)));
	}
	
	public static void updateLives(ServerPlayerEntity player, int newLives) {
		PlayerData data = getData(player);
		data.setLives(newLives);
		updateData(player);
		player.refreshDisplayName();
	}
	
	public static void tradeLives(ServerPlayerEntity sender, ServerPlayerEntity receiver, int amount) {
		updateLives(sender, getData(sender).getLives() - amount);
		updateLives(receiver, getData(receiver).getLives() + amount);
	}
}
