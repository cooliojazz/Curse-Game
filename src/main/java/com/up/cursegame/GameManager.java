package com.up.cursegame;

import static com.up.cursegame.block.Shrine.TYPE_PROPERTY;
import com.up.cursegame.block.tileentity.ShrineEntity;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.network.DiscordPlayerDistancesPacket;
import com.up.cursegame.network.PacketHandlers;
import com.up.cursegame.network.PlayerActivePercentPacket;
import com.up.cursegame.ritual.RitualGenerators;
import com.up.cursegame.util.PlayerDataUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;

/**
 *
 * @author Ricky
 */
public class GameManager {
	
	private static final HashMap<Attribute, UUID> attributeUuids = new HashMap<>();
	private static final HashMap<Attribute, Double> attributeAmounts = new HashMap<>();
	static {
		addAttribute(Attributes.ATTACK_DAMAGE, 0.5);
		addAttribute(Attributes.ARMOR, 0.5);
		addAttribute(Attributes.ARMOR_TOUGHNESS, 0.25);
		addAttribute(Attributes.MOVEMENT_SPEED, 0.15);
	}
	
	private final MinecraftServer server;
	private boolean started = false;
	private int activeCount = 1;
	public int minLives = 100;
	public int maxLives = 300;
	private int minDuration = 100;
	private int maxDuration = 300;
	private long ticks = 0;
	private long lastActiveTick = 0;
	private long nextActiveTick = 0;
	public ArrayList<ShrineEntity> initializeEntities = new ArrayList<>();

	public GameManager(MinecraftServer server) {
		this.server = server;
	}
	
	public void start(int minLives, int maxLives, int minDuration, int maxDuration, int active, int border) {
		server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, server);
		ServerWorld world = server.getAllLevels().iterator().next();
		initializeEntities.forEach(s -> {
				s.setCureRitual(RitualGenerators.values()[world.getBlockState(s.getBlockPos()).getValue(TYPE_PROPERTY)].getGenerator().generate(CurseGameMod.game.getAllPlayers().size()));
				s.setChanged();
			});
		WorldBorder overworldBorder = world.getWorldBorder();
		overworldBorder.setSize(border);
		overworldBorder.setCenter(world.getSharedSpawnPos().getX(), world.getSharedSpawnPos().getY());
		this.minLives = minLives;
		this.maxLives = maxLives;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.activeCount = active;
		getAllPlayers().forEach(p -> {
				p.kill();
				p.setGameMode(GameType.SURVIVAL);
				PlayerDataUtil.getData(p).setCursed(true);
				PlayerDataUtil.getData(p).setActive(false);
				PlayerDataUtil.updateLives(p, (int)(Math.random() * (maxLives - minLives) + minLives));
			});
		ticks = 0;
		nextActiveTick = (int)(Math.random() * (maxDuration - minDuration)) + minDuration;
		started = true;
	}
	
	public void stop() {
		server.getPlayerList().broadcastMessage(new StringTextComponent("Game is over!"), ChatType.CHAT, Util.NIL_UUID);
		for (ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
			if (PlayerDataUtil.isAlive(player)) {
				player.connection.send(new STitlePacket(STitlePacket.Type.TITLE, new StringTextComponent("You Won!")));
				player.connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, new StringTextComponent("*~-~-*~*-~-~*")));
			} else {
				player.connection.send(new STitlePacket(STitlePacket.Type.TITLE, new StringTextComponent("You Lost!")));
				player.connection.send(new STitlePacket(STitlePacket.Type.SUBTITLE, new StringTextComponent("Better luck next time")));
			}
		}
		started = false;
	}
	
	public void tick() {
		if (started) {
			ticks++;
			if (ticks > 1200 && isOver()) {
				stop();
				return;
			}
			if (ticks >= nextActiveTick) {
				lastActiveTick = nextActiveTick;
				nextActiveTick = ticks + (int)(Math.random() * (maxDuration - minDuration)) + minDuration;
				
				List<ServerPlayerEntity> players = getAllPlayers();
				players.stream().filter(p -> PlayerDataUtil.getData(p).isActive()).forEach(p -> deactivatePlayer(p));
				if (isOver()) {
					stop();
					return;
				}
				List<ServerPlayerEntity> alivePlayers = players.stream().filter(p -> PlayerDataUtil.isAlive(p)).collect(Collectors.toList());
				for (int i = 0; i < activeCount; i++) {
					ServerPlayerEntity player = alivePlayers.get((int)(Math.random() * alivePlayers.size()));
					activatePlayer(player);
				}
				players.forEach(p -> PlayerDataUtil.updateData(p));
			}
			if (ticks % 20 == 0) {
				double activePercent = (double)(ticks - lastActiveTick) / (nextActiveTick - lastActiveTick);
				HashMap<Attribute, AttributeModifier> attributeModifiers = new HashMap<>();
				for (Attribute attr : attributeUuids.keySet()) {
					attributeModifiers.put(attr, new AttributeModifier(attributeUuids.get(attr), attr.getRegistryName().getPath(), activePercent * attributeAmounts.get(attr), AttributeModifier.Operation.MULTIPLY_TOTAL));
				}
				getAllPlayers().forEach(player -> {
						if (PlayerDataUtil.getData(player).isActive()) {
							for (Attribute attr : attributeUuids.keySet()) {
								player.getAttribute(attr).removeModifier(attributeModifiers.get(attr));
								player.getAttribute(attr).addTransientModifier(attributeModifiers.get(attr));
							}
							PacketHandlers.send(player, new PlayerActivePercentPacket(activePercent));
						}
					});
			}
		}
		if (ticks % 5 == 0) {
			List<ServerPlayerEntity> players = getAllPlayers();
			players.forEach(player -> {
//					if (CurseGameMod.serverDiscord.getPlayerManager().getPlayerByUuid(player.getUUID()) != null) {
//						updateDistances(player);
//					}
				});
		}
	}
	
	public void updateDistances(ServerPlayerEntity player) {
		DiscordPlayerDistancesPacket packet = new DiscordPlayerDistancesPacket();
		getAllPlayers().forEach(p -> {
				if (started && player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
					if (p.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && player.level == p.level) {
						packet.addPlayerDistance(p.getUUID(), p.position().distanceTo(player.position()));
					} else {
						packet.addPlayerDistance(p.getUUID(), Double.MAX_VALUE);
					}
				} else {
					packet.addPlayerDistance(p.getUUID(), 0);
				}
			});
		PacketHandlers.send(player, packet);
	}
	
	private void activatePlayer(ServerPlayerEntity player) {
		PlayerData data = PlayerDataUtil.getData(player);
		data.setActive(true);
		data.setPrevLives(data.getLives());
		PlayerDataUtil.updateLives(player, 0);
		player.sendMessage(new StringTextComponent("The curse activates inside of you!").withStyle(TextFormatting.RED), Util.NIL_UUID);
	}
	
	private void deactivatePlayer(ServerPlayerEntity player) {
		PlayerData data = PlayerDataUtil.getData(player);
		data.setActive(false);
		if (!PlayerDataUtil.isAlive(player)) {
			player.kill();
			PlayerDataUtil.updateData(player);
		} else {
			int prev = data.getPrevLives();
			data.setPrevLives(-1);
			PlayerDataUtil.updateLives(player, data.getLives() + prev);
		}
		player.sendMessage(new StringTextComponent("The curse recedes for now...").withStyle(TextFormatting.DARK_BLUE), Util.NIL_UUID);
	}

	public boolean isStarted() {
		return started;
	}
	
	private boolean isOver() {
		return !getAllPlayers().stream().filter(p -> PlayerDataUtil.isAlive(p) && PlayerDataUtil.getData(p).isCursed()).findAny().isPresent();
	}
	
	//TODO: Remove these now that they are direct references to MinecraftServer methods lol
	public List<ServerPlayerEntity> getAllPlayers() {
		return server.getPlayerList().getPlayers();
	}
	
	public ServerPlayerEntity getPlayerByUuid(UUID uuid) {
		return server.getPlayerList().getPlayer(uuid);
	}
	
	public void curePlayersAt(BlockPos pos, double range, int players) {
		getAllPlayers().stream()
				.filter(p -> p.getPosition(0).distanceTo(Vector3d.atCenterOf(pos)) <= range)
				.sorted((p1, p2) -> (int)(p1.getPosition(0).distanceTo(Vector3d.atCenterOf(pos)) - p2.getPosition(0).distanceTo(Vector3d.atCenterOf(pos))))
				.limit(players)
				.forEach(p -> {
					PlayerDataUtil.updateCursed(p, false);
					p.sendMessage(new StringTextComponent("You've been cured!").withStyle(TextFormatting.UNDERLINE).withStyle(TextFormatting.GOLD), Util.NIL_UUID);
				});
	}
	
	private static void addAttribute(Attribute attr, double amount) {
		attributeUuids.put(attr, UUID.randomUUID());
		attributeAmounts.put(attr, amount);
	}
}
