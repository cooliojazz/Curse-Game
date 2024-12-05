package com.up.cursegame.discord;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Ricky
 */
public class DiscordPlayerManager {
	
	private HashMap<Long, DiscordPlayer> discordMap = new HashMap<>();
	private HashMap<DiscordPlayer, Long> playerMap = new HashMap<>();
	
	public void add(long discordId, DiscordPlayer player) {
		discordMap.put(discordId, player);
		playerMap.put(player, discordId);
	}
	
	public void remove(long discordId) {
		playerMap.remove(discordMap.remove(discordId));
	}
	
	public void remove(DiscordPlayer player) {
		discordMap.remove(playerMap.remove(player));
	}
	
	public long getDiscordId(DiscordPlayer player) {
		return playerMap.get(player);
	}
	
	public DiscordPlayer getPlayer(long discordId) {
		return discordMap.get(discordId);
	}
	
	public Collection<DiscordPlayer> getPlayers() {
		return playerMap.keySet();
	}
	
	public DiscordPlayer getPlayerByUuid(UUID playerUuid) {
		return playerMap.keySet().stream().filter(player -> player.getUuid().equals(playerUuid)).findAny().orElse(null);
	}
}
