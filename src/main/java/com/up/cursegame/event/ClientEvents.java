package com.up.cursegame.event;

import com.up.cursegame.CurseGameMod;
import static com.up.cursegame.CurseGameMod.MOD_ID;
import com.up.cursegame.block.tileentity.ShrineEntity;
import com.up.cursegame.gui.LivesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientEvents {
	
	public static KeyBinding muteBinding = new KeyBinding("key." + MOD_ID + ".mute.desc", GLFW.GLFW_KEY_M, "key." + MOD_ID + ".category");
	public static KeyBinding deafBinding = new KeyBinding("key." + MOD_ID + ".deaf.desc", GLFW.GLFW_KEY_N, "key." + MOD_ID + ".category");
	public static KeyBinding livesBinding = new KeyBinding("key." + MOD_ID + ".lives.desc", GLFW.GLFW_KEY_L, "key." + MOD_ID + ".category");
	
	public static void clientSetup() {
		ClientRegistry.registerKeyBinding(muteBinding);
		ClientRegistry.registerKeyBinding(deafBinding);
		ClientRegistry.registerKeyBinding(livesBinding);
        ClientRegistry.bindTileEntityRenderer(ShrineEntity.TYPE, ShrineEntity.Renderer::new);
	}
	
	@SubscribeEvent
	public static void onClientLeaveServer(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		if (CurseGameMod.clientDiscord.isReady()) CurseGameMod.clientDiscord.disable();
	}
	
	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if (muteBinding.isDown()) {
			CurseGameMod.clientDiscord.getCore().voiceManager().setSelfMute(!CurseGameMod.clientDiscord.getCore().voiceManager().isSelfMute());
		}
		if (deafBinding.isDown()) {
			CurseGameMod.clientDiscord.getCore().voiceManager().setSelfDeaf(!CurseGameMod.clientDiscord.getCore().voiceManager().isSelfDeaf());
		}
		if (livesBinding.isDown()) {
			Minecraft.getInstance().setScreen(new LivesGui());
		}
	}
	
}
