package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class DiscordLobbyPacket {
	
	private long lobbyId;
	private String secret;

	public DiscordLobbyPacket() {
	}

	public DiscordLobbyPacket(long lobbyId, String secret) {
		this.lobbyId = lobbyId;
		this.secret = secret;
	}
	
	public static void encode(DiscordLobbyPacket packet, PacketBuffer buffer) {
		buffer.writeLong(packet.lobbyId);
		buffer.writeUtf(packet.secret);
	}
	
	public static DiscordLobbyPacket decode(PacketBuffer buffer) {
		DiscordLobbyPacket packet = new DiscordLobbyPacket();
		packet.lobbyId = buffer.readLong();
		packet.secret = buffer.readUtf();
		return packet;
	}
	
	public static void handle(DiscordLobbyPacket packet, Supplier<NetworkEvent.Context> ctx) {
		CurseGameMod.clientDiscord.enable(packet.lobbyId, packet.secret);
		PacketHandlers.send(new DiscordPlayerJoinPacket(CurseGameMod.clientDiscord.getUser().getUserId()));
		ctx.get().setPacketHandled(true);
	}
}
