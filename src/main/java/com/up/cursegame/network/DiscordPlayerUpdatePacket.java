package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.discord.DiscordPlayer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class DiscordPlayerUpdatePacket {
	
	private long discordId;
	private UUID playerUuid;
	private String playerName;
	private UpdateAction action;

	public DiscordPlayerUpdatePacket() {
	}

	public DiscordPlayerUpdatePacket(long discordId, UUID playerUuid, String playerName, UpdateAction action) {
		this.discordId = discordId;
		this.playerUuid = playerUuid;
		this.action = action;
		this.playerName = playerName;
	}
	
	public static void encode(DiscordPlayerUpdatePacket packet, PacketBuffer buffer) {
		buffer.writeLong(packet.discordId);
		buffer.writeUUID(packet.playerUuid);
		buffer.writeEnum(packet.action);
		buffer.writeUtf(packet.playerName);
	}
	
	public static DiscordPlayerUpdatePacket decode(PacketBuffer buffer) {
		DiscordPlayerUpdatePacket packet = new DiscordPlayerUpdatePacket();
		packet.discordId = buffer.readLong();
		packet.playerUuid = buffer.readUUID();
		packet.action = buffer.readEnum(UpdateAction.class);
		packet.playerName = buffer.readUtf();
		return packet;
	}
	
	public static void handle(DiscordPlayerUpdatePacket packet, Supplier<NetworkEvent.Context> ctx) {
		if (packet.action == UpdateAction.ADD) {
			CurseGameMod.clientDiscord.getPlayerManager().add(packet.discordId, new DiscordPlayer(packet.playerUuid, packet.playerName));
		} else if (packet.action == UpdateAction.REMOVE) {
			CurseGameMod.clientDiscord.getPlayerManager().remove(packet.discordId);
		}
		ctx.get().setPacketHandled(true);
	}
	
	public static enum UpdateAction {
		ADD, REMOVE
	}
}
