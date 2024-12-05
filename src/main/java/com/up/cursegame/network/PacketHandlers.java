package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 *
 * @author Ricky
 */
public class PacketHandlers {
	
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(CurseGameMod.MOD_ID, "network"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
		);
	
	public static void register() {
		INSTANCE.registerMessage(0, PlayerDataPacket.class, PlayerDataPacket::encode, PlayerDataPacket::decode, PlayerDataPacket::handle);
		INSTANCE.registerMessage(1, PlayerTradeLivesPacket.class, PlayerTradeLivesPacket::encode, PlayerTradeLivesPacket::decode, PlayerTradeLivesPacket::handle);
		INSTANCE.registerMessage(2, DiscordPlayerUpdatePacket.class, DiscordPlayerUpdatePacket::encode, DiscordPlayerUpdatePacket::decode, DiscordPlayerUpdatePacket::handle);
		INSTANCE.registerMessage(3, DiscordPlayerJoinPacket.class, DiscordPlayerJoinPacket::encode, DiscordPlayerJoinPacket::decode, DiscordPlayerJoinPacket::handle);
		INSTANCE.registerMessage(4, DiscordPlayerDistancesPacket.class, DiscordPlayerDistancesPacket::encode, DiscordPlayerDistancesPacket::decode, DiscordPlayerDistancesPacket::handle);
		INSTANCE.registerMessage(5, DiscordLobbyPacket.class, DiscordLobbyPacket::encode, DiscordLobbyPacket::decode, DiscordLobbyPacket::handle);
		INSTANCE.registerMessage(6, PlayerActivePercentPacket.class, PlayerActivePercentPacket::encode, PlayerActivePercentPacket::decode, PlayerActivePercentPacket::handle);
	}
	
	public static void sendToServer(Object packet) {
		INSTANCE.sendToServer(packet);
	}
	
	public static void sendToPlayer(ServerPlayerEntity player, Object packet) {
//		INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> player.level.getChunkAt(player.blockPosition())), packet);
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
	}
	
	public static void sendToPlayers(MinecraftServer server, Object packet) {
		server.getAllLevels().forEach(world -> world.players().forEach(player -> sendToPlayer(player, packet)));
	}
	
	public static void send(ServerPlayerEntity player, PlayerDataPacket packet) {
		sendToPlayer(player, packet);
	}
	
//	public static void sendAll(ServerWorld world, PlayerDataPacket packet) {
//		world.players().forEach(player -> sendToPlayer(player, packet));
//	}
	
	public static void sendAll(MinecraftServer server, PlayerDataPacket packet) {
		sendToPlayers(server, packet);
	}
	
	public static void send(ServerPlayerEntity player, DiscordPlayerUpdatePacket packet) {
		sendToPlayer(player, packet);
	}
	
	public static void sendAll(MinecraftServer server, DiscordPlayerUpdatePacket packet) {
		sendToPlayers(server, packet);
	}
	
	public static void send(ServerPlayerEntity player, DiscordPlayerDistancesPacket packet) {
		sendToPlayer(player, packet);
	}
	
	public static void send(ServerPlayerEntity player, DiscordLobbyPacket packet) {
		sendToPlayer(player, packet);
	}
	
	public static void send(ServerPlayerEntity player, PlayerActivePercentPacket packet) {
		sendToPlayer(player, packet);
	}
	
	public static void send(PlayerTradeLivesPacket packet) {
		sendToServer(packet);
	}
	
	public static void send(DiscordPlayerJoinPacket packet) {
		sendToServer(packet);
	}
}
