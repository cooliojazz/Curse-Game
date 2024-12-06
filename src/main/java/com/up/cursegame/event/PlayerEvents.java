package com.up.cursegame.event;

import com.up.cursegame.CurseGameMod;
import com.up.cursegame.discord.DiscordPlayer;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.network.DiscordLobbyPacket;
import com.up.cursegame.network.DiscordPlayerUpdatePacket;
import com.up.cursegame.network.PlayerDataPacket;
import com.up.cursegame.network.PacketHandlers;
import com.up.cursegame.util.PlayerDataUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber
public class PlayerEvents {
	
	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		PlayerData data = player.getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		PacketHandlers.send(player, new PlayerDataPacket(data));
		
//		for (DiscordPlayer dp : CurseGameMod.serverDiscord.getPlayerManager().getPlayers()) {
//			PacketHandlers.send(player, new DiscordPlayerUpdatePacket(CurseGameMod.serverDiscord.getPlayerManager().getDiscordId(dp), dp.getUuid(), dp.getName(), DiscordPlayerUpdatePacket.UpdateAction.ADD));
//		}
//		if (!CurseGameMod.serverDiscord.isReady()) CurseGameMod.serverDiscord.enable();
//		PacketHandlers.send(player, new DiscordLobbyPacket(CurseGameMod.serverDiscord.getLobbyId(), CurseGameMod.serverDiscord.getSecret()));
		
		// TODO: Don't forget to remove this quick hack to disable auth for local servers
//		event.getPlayer().level.getServer().setUsesAuthentication(false);
	}
	
	@SubscribeEvent
	public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.getEntity().level.isClientSide()) {
			ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
			PacketHandlers.send(player, new PlayerDataPacket(player.getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null)));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		PlayerData oldData = event.getOriginal().getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		PlayerData newData = event.getPlayer().getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		PlayerData.storage.readNBT(null, newData, null, PlayerData.storage.writeNBT(null, oldData, null));
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!player.level.isClientSide()) {
			PacketHandlers.send((ServerPlayerEntity)player, new PlayerDataPacket(PlayerDataUtil.getData(player)));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (!event.getEntity().level.isClientSide()) {
			if (event.getEntity() instanceof PlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
				int lives = PlayerDataUtil.getData(player).getLives();
				lives--;
				if (lives < 1) {
					player.setGameMode(GameType.SPECTATOR);
				}
				PlayerDataUtil.updateLives(player, lives);
				if (event.getSource().getEntity() instanceof PlayerEntity) {
					ServerPlayerEntity killer = (ServerPlayerEntity)event.getSource().getEntity();
					if (PlayerDataUtil.getData(killer).isActive()) {
						PlayerDataUtil.updateLives(killer, PlayerDataUtil.getData(killer).getLives() + 1);
					}
				}
			}
		}
	}
	
//	@SubscribeEvent
//	public static void onNameFormat(PlayerEvent.NameFormat event) {
//		//if (!event.getEntity().level.isClientSide) {
//			if (event.getEntity() instanceof PlayerEntity) {
//				PlayerData data = PlayerDataUtil.getData((PlayerEntity)event.getEntity());
//				TextFormatting style = TextFormatting.GREEN;
//				if (data.getLives() < CurseGameMod.game.maxLives / 3) {
//					style = TextFormatting.RED;
//				} else if (data.getLives() < CurseGameMod.game.maxLives * 2 / 3) {
//					style = TextFormatting.YELLOW;
//				}
//				event.setDisplayname(new StringTextComponent(event.getUsername().getContents()).withStyle(style));
//			}
//		//}
//	}
	
	@SubscribeEvent
	public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof PlayerEntity) {
			World world = event.getEntity().level;
			if (!world.isClientSide) {
//				DiscordPlayer player = CurseGameMod.serverDiscord.getPlayerManager().getPlayerByUuid(event.getEntity().getUUID());
//				if (player != null) {
//					PacketHandlers.sendAll(world.getServer(), new DiscordPlayerUpdatePacket(CurseGameMod.serverDiscord.getPlayerManager().getDiscordId(player), player.getUuid(), player.getName(), DiscordPlayerUpdatePacket.UpdateAction.REMOVE));
//					CurseGameMod.serverDiscord.getPlayerManager().remove(player);
//				}
//				if (CurseGameMod.serverDiscord.getPlayerManager().getPlayers().isEmpty()) CurseGameMod.serverDiscord.disable();
			}
		}
	}
	
}
