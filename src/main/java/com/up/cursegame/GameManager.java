package com.up.cursegame;

import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.network.DiscordPlayerDistancePacket;
import com.up.cursegame.network.PacketHandlers;
import com.up.cursegame.util.DataUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;

/**
 *
 * @author Ricky
 */
public class GameManager {
	
	private MinecraftServer server;
	private boolean started = false;
	private int activeCount = 1;
	public int minLives = 100;
	public int maxLives = 300;
	private int minDuration = 100;
	private int maxDuration = 300;
	private long ticks = 0;
	private long nextTick = 0;
	
	public void start(MinecraftServer server, int minLives, int maxLives, int minDuration, int maxDuration, int active) {
		this.server = server;
		this.minLives = minLives;
		this.maxLives = maxLives;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		getAllPlayers().forEach(p -> {
				DataUtil.getData(p).setCursed(true);
				DataUtil.getData(p).setActive(false);
				DataUtil.updateLives(p, (int)(Math.random() * (maxLives - minLives) + minLives));
			});
		nextTick = (int)(Math.random() * (maxDuration - minDuration)) + minDuration;
		started = true;
	}
	
	public void tick() {
		if (started) {
			ticks++;
			if (ticks >= nextTick) {
				server.sendMessage(new StringTextComponent("Choosing new cursed"), Util.NIL_UUID);
				nextTick = ticks + (int)(Math.random() * (maxDuration - minDuration)) + minDuration;
				
				List<ServerPlayerEntity> players = getAllPlayers();
				players.forEach(p -> DataUtil.getData(p).setActive(false));
				for (int i = 0; i < activeCount; i++) {
					ServerPlayerEntity player = players.get((int)(Math.random() *  players.size()));
					activatePlayer(player);
				}
				players.forEach(p -> DataUtil.updateData(p));
			}
			if (ticks % 10 == 0) {
				if (isOver()) {
					server.sendMessage(new StringTextComponent("Game is over!"), Util.NIL_UUID);
				}
			}
		}
	}
	
	public void updateDistances(ServerPlayerEntity player) {
		getAllPlayers().forEach(p -> {
				if (started && p.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
					if (player.level == p.level) {
						PacketHandlers.send(p, new DiscordPlayerDistancePacket(player.getUUID(), p.getPosition(0).distanceTo(player.getPosition(0))));
					} else {
						PacketHandlers.send(p, new DiscordPlayerDistancePacket(player.getUUID(), Double.MAX_VALUE));
					}
				} else {
					PacketHandlers.send(p, new DiscordPlayerDistancePacket(player.getUUID(), 0));
				}
			});
	}
	
	private void activatePlayer(ServerPlayerEntity player) {
		PlayerData data = DataUtil.getData(player);
		data.setActive(true);
		DataUtil.updateLives(player, 0);
		player.sendMessage(new StringTextComponent("The curse activates inside of you!").withStyle(TextFormatting.RED), Util.NIL_UUID);
	}
	
	private boolean isOver() {
		return !streamAllPlayers().filter(p -> DataUtil.getData(p).isCursed()).findAny().isPresent();
	}
	
	private Stream<ServerPlayerEntity> streamAllPlayers() {
//		return StreamSupport.stream(server.getAllLevels().spliterator(), false).map(world -> world.players().stream()).reduce(Stream.empty(), (s1, s2) -> Stream.concat(s1, s2));
		return getAllPlayers().stream();
	}
	
	private List<ServerPlayerEntity> getAllPlayers() {
		ArrayList<ServerPlayerEntity> players = new ArrayList<>();
		StreamSupport.stream(server.getAllLevels().spliterator(), false).forEach(world -> players.addAll(world.players()));
		return players;
	}
}
