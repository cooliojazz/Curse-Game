package com.up.cursegame.discord;

import java.util.UUID;
import net.minecraft.entity.player.PlayerEntity;

/**
 *
 * @author Ricky
 */
public class DiscordPlayer {
	
	private UUID uuid;
	private String name;
	private double distance = -1;

	public DiscordPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public static DiscordPlayer fromPlayerEntity(PlayerEntity player) {
		return new DiscordPlayer(player.getUUID(), player.getDisplayName().getString());
	}
}
