package com.up.cursegame.network;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.util.PlayerDataUtil;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 *
 * @author Ricky
 */
public class PlayerTradeLivesPacket {
	
	private int lives;
	private UUID reciever;

	public PlayerTradeLivesPacket() {
	}

	public PlayerTradeLivesPacket(int lives, UUID reciever) {
		this.lives = lives;
		this.reciever = reciever;
	}
	
	public static void encode(PlayerTradeLivesPacket packet, PacketBuffer buffer) {
		buffer.writeInt(packet.lives);
		buffer.writeUUID(packet.reciever);
	}
	
	public static PlayerTradeLivesPacket decode(PacketBuffer buffer) {
		PlayerTradeLivesPacket packet = new PlayerTradeLivesPacket();
		packet.lives = buffer.readInt();
		packet.reciever = buffer.readUUID();
		return packet;
	}
	
	public static void handle(PlayerTradeLivesPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
				ServerPlayerEntity sender = ctx.get().getSender();
				ServerPlayerEntity receiver = (ServerPlayerEntity)CurseGameMod.game.getPlayerByUuid(packet.reciever);
				PlayerDataUtil.tradeLives(sender, receiver, packet.lives);
			});
		ctx.get().setPacketHandled(true);
	}
}
