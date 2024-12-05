package com.up.cursegame.event;

import com.up.cursegame.CurseGame;
import static com.up.cursegame.CurseGame.MOD_ID;
import com.up.cursegame.block.tileentity.ShrineEntity;
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
	
	public static void clientSetup() {
		ClientRegistry.registerKeyBinding(muteBinding);
		ClientRegistry.registerKeyBinding(deafBinding);
        ClientRegistry.bindTileEntityRenderer(ShrineEntity.TYPE, ShrineEntity.Renderer::new);
	}
	
	@SubscribeEvent
	public static void onClientLeaveServer(ClientPlayerNetworkEvent.LoggedOutEvent event) {
		if (CurseGame.clientDiscord.isReady()) CurseGame.clientDiscord.disable();
	}
	
	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event) {
		if (muteBinding.isDown()) {
			CurseGame.clientDiscord.getCore().voiceManager().setSelfMute(!CurseGame.clientDiscord.getCore().voiceManager().isSelfMute());
		}
		if (deafBinding.isDown()) {
			CurseGame.clientDiscord.getCore().voiceManager().setSelfDeaf(!CurseGame.clientDiscord.getCore().voiceManager().isSelfDeaf());
		}
	}
	
}
