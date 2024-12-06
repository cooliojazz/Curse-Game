package com.up.cursegame;

import com.up.cursegame.event.ClientEvents;
import com.up.cursegame.discord.DiscordClientManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.discord.DiscordServerManager;
import com.up.cursegame.network.PacketHandlers;
import java.util.Arrays;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CurseGameMod.MOD_ID)
@Mod.EventBusSubscriber
public class CurseGameMod {
	
	public static final String MOD_ID = "cursegame";
	private static final Logger LOGGER = LogManager.getLogger();
	public static DiscordClientManager clientDiscord;
//	public static DiscordServerManager serverDiscord;
	public static GameManager game;
	
	public CurseGameMod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void setup(final FMLCommonSetupEvent event) {
		PlayerData.register();
		PacketHandlers.register();
	}
	
	private void clientSetup(final FMLClientSetupEvent event) {
		clientDiscord = new DiscordClientManager();
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::clientSetup);
	}
	
	@SubscribeEvent
	public static void onServerStart(FMLServerStartingEvent event) {
//		serverDiscord = new DiscordServerManager();
		game = new GameManager(event.getServer());
	}
	
	@SubscribeEvent
	public static void onServerStop(FMLServerStoppingEvent event) {
//		if (CurseGameMod.serverDiscord.isReady()) serverDiscord.disable();
	}
	
	@SubscribeEvent
	public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> capEvent) {
		ResourceLocation capLoc = new ResourceLocation(CurseGameMod.MOD_ID, "data");
		if (capEvent.getObject() instanceof PlayerEntity) {
			capEvent.addCapability(capLoc, new PlayerDataCapabilityProvider());
		}
	}
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent tickEvent) {
		if (tickEvent.phase == TickEvent.Phase.END) game.tick();
	}
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		Arrays.asList(CurseCommands.commands).forEach(event.getDispatcher()::register);
	}
}
