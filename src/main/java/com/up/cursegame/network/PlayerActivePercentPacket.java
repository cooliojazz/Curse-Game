package com.up.cursegame.network;

import com.up.cursegame.gui.HUD;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class PlayerActivePercentPacket {
	
	private double activePercent;

	public PlayerActivePercentPacket(double activePercent) {
		this.activePercent = activePercent;
	}
	
	public static void encode(PlayerActivePercentPacket packet, PacketBuffer buffer) {
		buffer.writeDouble(packet.activePercent);
	}
	
	public static PlayerActivePercentPacket decode(PacketBuffer buffer) {
		PlayerActivePercentPacket packet = new PlayerActivePercentPacket(0);
		packet.activePercent = buffer.readDouble();
		return packet;
	}
	
	public static void handle(PlayerActivePercentPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					HUD.activePercent = packet.activePercent;
				});
			});
		ctx.get().setPacketHandled(true);
	}
}
