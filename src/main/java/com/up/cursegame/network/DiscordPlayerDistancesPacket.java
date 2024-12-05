package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.discord.DiscordClientManager;
import com.up.cursegame.discord.DiscordPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class DiscordPlayerDistancesPacket {
	
	private Map<UUID, Double> distances = new HashMap<>();

	public DiscordPlayerDistancesPacket() { }
	
	public void addPlayerDistance(UUID playerUuid, double distance) {
		distances.put(playerUuid, distance);
	}
	
	public static void encode(DiscordPlayerDistancesPacket packet, PacketBuffer buffer) {
		CompoundNBT tag = new CompoundNBT();
		for (Map.Entry<UUID, Double> entry : packet.distances.entrySet()) {
			tag.putDouble(entry.getKey().toString(), entry.getValue());
		}
		buffer.writeNbt(tag);
	}
	
	public static DiscordPlayerDistancesPacket decode(PacketBuffer buffer) {
		DiscordPlayerDistancesPacket packet = new DiscordPlayerDistancesPacket();
		CompoundNBT tag = buffer.readNbt();
		for (String uuid : tag.getAllKeys()) {
			packet.distances.put(UUID.fromString(uuid), tag.getDouble(uuid));
		}
		return packet;
	}
	
	public static void handle(DiscordPlayerDistancesPacket packet, Supplier<NetworkEvent.Context> ctx) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				for (Map.Entry<UUID, Double> entry : packet.distances.entrySet()) {
					DiscordPlayer player = CurseGameMod.clientDiscord.getPlayerManager().getPlayerByUuid(entry.getKey());
					if (player != null && !player.getUuid().equals(Minecraft.getInstance().player.getUUID())) {
						CurseGameMod.clientDiscord.getCore().voiceManager().setLocalVolume(CurseGameMod.clientDiscord.getPlayerManager().getDiscordId(player), (int)Math.max(0, Math.min(100, 100 * (1 - (entry.getValue() - DiscordClientManager.MIN_VOLUME_RANGE) / (DiscordClientManager.MAX_VOLUME_RANGE - DiscordClientManager.MIN_VOLUME_RANGE)))));
					}
					player.setDistance(entry.getValue());
				}
			});
		ctx.get().setPacketHandled(true);
	}
}
