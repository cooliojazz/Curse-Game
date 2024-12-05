package com.up.cursegame;

import com.up.cursegame.event.ClientEvents;
import com.up.cursegame.discord.DiscordClientManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.discord.DiscordServerManager;
import com.up.cursegame.network.PacketHandlers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

@Mod(CurseGame.MOD_ID)
@Mod.EventBusSubscriber
public class CurseGame {
	
	public static final String MOD_ID = "cursegame";
	private static final Logger LOGGER = LogManager.getLogger();
	public static DiscordClientManager clientDiscord;
	public static DiscordServerManager serverDiscord;
	public static GameManager game = new GameManager();
	
	public CurseGame() {
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
		serverDiscord = new DiscordServerManager();
	}
	
	@SubscribeEvent
	public static void onServerStop(FMLServerStoppingEvent event) {
		if (CurseGame.serverDiscord.isReady()) serverDiscord.disable();
	}
	
	@SubscribeEvent
	public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> capEvent) {
		ResourceLocation capLoc = new ResourceLocation(CurseGame.MOD_ID, "data");
		if (capEvent.getObject() instanceof PlayerEntity) {
			capEvent.addCapability(capLoc, new PlayerDataCapabilityProvider());
		}
	}
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent tickEvent) {
		if (tickEvent.getPhase() == EventPriority.NORMAL) game.tick();
	}
	
	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		for (LiteralArgumentBuilder command : CurseCommands.commands) event.getDispatcher().register(command);
	}
	
	// Idea stuff
	// - The group just murdered the dragon and got cursed, set back to the begining of
	//     the adventure with an additional bloodthirst working around them. The only
	//     hope is to perform one of the curse cleansing rituals or cleanse the curse
	//     by other means before they are all consumed.
	// - Ways to clear the curse (win (specifically when the entire curse is cleansed))
	//   - Harder the less people there are? Or would the opposite be better so dead people have to wait less lol?
	//   - > 75% of group
	//     - Ocean Monument
	//     - Bastion
	//   - > 1 player
	//     - Respawn dragon
	//   - Wait, so what if the following provided a cure for that many people at a time, not a minimum count of them (!!!!)
	//     - 100% of group
	//       - Respawn dragon
	//     - 50% of group
	//       - Ocean Monument
	//       - Woodland Mansion
    //	     // - Bastion
	//     - 1 player
	//       - Forest temple
	//       - Desert temple
	//       - Totem of undying?
	// - The cursed stuff
	//   - Gets reduced to 0 lives for the duration of the curse and must get at least one life back to survive
	//   - Can get lives back by killing others
	//   - 
}
