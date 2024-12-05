package com.up.cursegame.network;

import com.up.cursegame.CurseGame;
import com.up.cursegame.discord.DiscordClientManager;
import com.up.cursegame.discord.DiscordPlayer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class DiscordPlayerDistancePacket {
	
	private UUID playerUuid;
	private double distance;

	public DiscordPlayerDistancePacket() {
	}

	public DiscordPlayerDistancePacket(UUID playerUuid, double distance) {
		this.playerUuid = playerUuid;
		this.distance = distance;
	}
	
	public static void encode(DiscordPlayerDistancePacket packet, PacketBuffer buffer) {
		buffer.writeUUID(packet.playerUuid);
		buffer.writeDouble(packet.distance);
	}
	
	public static DiscordPlayerDistancePacket decode(PacketBuffer buffer) {
		DiscordPlayerDistancePacket packet = new DiscordPlayerDistancePacket();
		packet.playerUuid = buffer.readUUID();
		packet.distance = buffer.readDouble();
		return packet;
	}
	
	public static void handle(DiscordPlayerDistancePacket packet, Supplier<NetworkEvent.Context> ctx) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				DiscordPlayer player = CurseGame.clientDiscord.getPlayerManager().getPlayerByUuid(packet.playerUuid);
				if (player != null && !player.getUuid().equals(Minecraft.getInstance().player.getUUID())) {
					CurseGame.clientDiscord.getCore().voiceManager().setLocalVolume(CurseGame.clientDiscord.getPlayerManager().getDiscordId(player), (int)Math.max(0, Math.min(100, 100 * (1 - (packet.distance - DiscordClientManager.MIN_VOLUME_RANGE) / (DiscordClientManager.MAX_VOLUME_RANGE - DiscordClientManager.MIN_VOLUME_RANGE)))));
				}
				player.setDistance(packet.distance);
			});
		ctx.get().setPacketHandled(true);
	}
}
