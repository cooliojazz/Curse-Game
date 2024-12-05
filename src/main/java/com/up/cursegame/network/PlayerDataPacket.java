package com.up.cursegame.network;

import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
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
public class PlayerDataPacket {
	
	private PlayerData data;

	public PlayerDataPacket(PlayerData data) {
		this.data = data;
	}
	
	public static void encode(PlayerDataPacket packet, PacketBuffer buffer) {
		buffer.writeNbt((CompoundNBT)PlayerData.storage.writeNBT(null, packet.data, null));
	}
	
	public static PlayerDataPacket decode(PacketBuffer buffer) {
		PlayerDataPacket packet = new PlayerDataPacket(new PlayerData());
		PlayerData.storage.readNBT(null, packet.data, null, buffer.readNbt());
		return packet;
	}
	
	public static void handle(PlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					PlayerData data = Minecraft.getInstance().player.getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
					data.setPrevLives(packet.data.getPrevLives());
					data.setLives(packet.data.getLives());
					data.setCursed(packet.data.isCursed());
					data.setActive(packet.data.isActive());
				});
			});
		ctx.get().setPacketHandled(true);
	}
}
