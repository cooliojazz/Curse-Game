package com.up.cursegame.event;

import com.up.cursegame.CurseGame;
import com.up.cursegame.discord.DiscordPlayer;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.network.DiscordLobbyPacket;
import com.up.cursegame.network.DiscordPlayerUpdatePacket;
import com.up.cursegame.network.PlayerDataPacket;
import com.up.cursegame.network.PacketHandlers;
import com.up.cursegame.util.DataUtil;
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
//		if (data.getLives() == 0) {
//			data.setLives(5);
//			PacketHandlers.sendAll((ServerWorld)player.level, new PlayerDataPacket(data));
//		}
		PacketHandlers.send(player, new PlayerDataPacket(data));
		
		for (DiscordPlayer dp : CurseGame.serverDiscord.getPlayerManager().getPlayers()) {
			PacketHandlers.send(player, new DiscordPlayerUpdatePacket(CurseGame.serverDiscord.getPlayerManager().getDiscordId(dp), dp.getUuid(), dp.getName(), DiscordPlayerUpdatePacket.UpdateAction.ADD));
		}
		if (!CurseGame.serverDiscord.isReady()) CurseGame.serverDiscord.enable();
		PacketHandlers.send(player, new DiscordLobbyPacket(CurseGame.serverDiscord.getLobbyId(), CurseGame.serverDiscord.getSecret()));
		
		// TODO: Don't forget to remove this quick hack to disable auth for local servers
		event.getPlayer().level.getServer().setUsesAuthentication(false);
	}
	
	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event) {
		PlayerData oldData = event.getOriginal().getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		PlayerData newData = event.getPlayer().getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		newData.setLives(oldData.getLives());
		newData.setCursed(oldData.isCursed());
		newData.setActive(oldData.isActive());
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		PlayerEntity player = event.getPlayer();
		if (!player.level.isClientSide()) {
			PacketHandlers.send((ServerPlayerEntity)player, new PlayerDataPacket(DataUtil.getData(player)));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		if (!event.getEntity().level.isClientSide()) {
			if (event.getEntity() instanceof PlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity)event.getEntity();
				int lives = DataUtil.getData(player).getLives();
				lives--;
				if (lives < 1) {
					player.setGameMode(GameType.SPECTATOR);
				}
				DataUtil.updateLives(player, lives);
				if (event.getSource().getEntity() instanceof PlayerEntity) {
					ServerPlayerEntity killer = (ServerPlayerEntity)event.getSource().getEntity();
					if (DataUtil.getData(killer).isActive()) {
						DataUtil.updateLives(killer, DataUtil.getData(killer).getLives() + 1);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onNameFormat(PlayerEvent.NameFormat event) {
		if (event.getEntity() instanceof PlayerEntity) {
			PlayerData data = DataUtil.getData((PlayerEntity)event.getEntity());
			TextFormatting style = TextFormatting.GREEN;
			if (data.getLives() < CurseGame.game.maxLives / 3) {
				style = TextFormatting.RED;
			} else if (data.getLives() < CurseGame.game.maxLives * 2 / 3) {
				style = TextFormatting.YELLOW;
			}
			event.setDisplayname(new StringTextComponent(event.getUsername().getContents()).withStyle(style));
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.getEntity() instanceof PlayerEntity) {
			World world = event.getEntity().level;
			if (!world.isClientSide) {
				DiscordPlayer player = CurseGame.serverDiscord.getPlayerManager().getPlayerByUuid(event.getEntity().getUUID());
				if (player != null) {
					PacketHandlers.sendAll(world.getServer(), new DiscordPlayerUpdatePacket(CurseGame.serverDiscord.getPlayerManager().getDiscordId(player), player.getUuid(), player.getName(), DiscordPlayerUpdatePacket.UpdateAction.REMOVE));
					CurseGame.serverDiscord.getPlayerManager().remove(player);
				}
				if (CurseGame.serverDiscord.getPlayerManager().getPlayers().isEmpty()) CurseGame.serverDiscord.disable();
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerMove(PlayerEvent.LivingUpdateEvent event) {
		if (!event.getEntity().level.isClientSide && event.getEntity().level.getGameTime() % 20 == 0) {
			if (event.getEntity() instanceof PlayerEntity && CurseGame.serverDiscord.getPlayerManager().getPlayerByUuid(event.getEntity().getUUID()) != null) {
				CurseGame.game.updateDistances((ServerPlayerEntity)event.getEntity());
			}
		}
	}
	
}
