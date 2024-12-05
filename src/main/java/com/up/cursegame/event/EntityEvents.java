package com.up.cursegame.event;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber
public class EntityEvents {
	
//	@SubscribeEvent
//	public static void onPlayerDeath(LivingDeathEvent event) {
//		if (event.getEntity() instanceof PlayerEntity) {
//			PlayerEntity player = (PlayerEntity)event.getEntity();
//			int lives = LivesUtil.getLives(player).getLives();
//			lives--;
//			if (lives < 1) {
//				player.setGameMode(GameType.SPECTATOR);
//			}
//			LivesUtil.updateLives(player, lives);
//		}
//	}
	
	
	@SubscribeEvent
	public static void onEntitySpawn(EntityJoinWorldEvent event) {
//		EntityEvent q = new 
		if (event.getEntity() instanceof EnderDragonEntity) {
			if (!event.getWorld().isClientSide) {
				((ServerWorld)event.getWorld()).dragonFight().setDragonKilled((EnderDragonEntity)event.getEntity());
				event.getEntity().kill();
			}
		}
	}
	
	
//	@SubscribeEvent
//	public static void onLeaveWorld(EntityLeaveWorldEvent event) {
//		if (event.getEntity() instanceof PlayerEntity) {
//			if (event.getWorld().isClientSide) {
//				if (event.getEntity().getId() == Minecraft.getInstance().player.getId()){
//					CurseGame.discord.disable();
//				}
//			} else {
//				DiscordPlayer player = CurseGame.discord.getPlayerManager().getPlayerById(event.getEntity().getId());
//				PacketHandlers.sendAll(event.getWorld().getServer(), new DiscordPlayerUpdatePacket(CurseGame.discord.getPlayerManager().getDiscordId(player), player.getId(), player.getName(), DiscordPlayerUpdatePacket.UpdateAction.REMOVE));
//				CurseGame.discord.getPlayerManager().remove(player);
//			}
//		}
//	}
	
//	@SubscribeEvent
//	public static void onPlayerMove(PlayerEvent.LivingUpdateEvent event) {
//		if (!event.getEntity().level.isClientSide && event.getEntity() instanceof PlayerEntity && CurseGame.discord.isReady() && CurseGame.discord.getPlayerManager().getPlayerByUuid(event.getEntity().getUUID()) != null) {
//			PlayerEntity player = (PlayerEntity)event.getEntity();
//			event.getEntity().level.players().forEach(p ->
//					PacketHandlers.send((ServerPlayerEntity)p, new DiscordPlayerDistancePacket(player.getUUID(), p.getPosition(0).distanceTo(player.getPosition(0))))
//				);
//		}
//	}
	
}
