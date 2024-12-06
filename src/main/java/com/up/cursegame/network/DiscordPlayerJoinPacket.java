package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.discord.DiscordPlayer;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class DiscordPlayerJoinPacket {
	
	private long discordId;

	public DiscordPlayerJoinPacket() {
	}

	public DiscordPlayerJoinPacket(long discordId) {
		this.discordId = discordId;
	}
	
	public static void encode(DiscordPlayerJoinPacket packet, PacketBuffer buffer) {
		buffer.writeLong(packet.discordId);
	}
	
	public static DiscordPlayerJoinPacket decode(PacketBuffer buffer) {
		DiscordPlayerJoinPacket packet = new DiscordPlayerJoinPacket();
		packet.discordId = buffer.readLong();
		return packet;
	}
	
	public static void handle(DiscordPlayerJoinPacket packet, Supplier<NetworkEvent.Context> ctx) {
		DiscordPlayer player = DiscordPlayer.fromPlayerEntity(ctx.get().getSender());
		ctx.get().enqueueWork(() -> {
				PacketHandlers.sendAll(ctx.get().getSender().getServer(), new DiscordPlayerUpdatePacket(packet.discordId, player.getUuid(), player.getName(), DiscordPlayerUpdatePacket.UpdateAction.ADD));
			});
//		CurseGameMod.serverDiscord.getPlayerManager().add(packet.discordId, player);
		ctx.get().setPacketHandled(true);
	}
}
