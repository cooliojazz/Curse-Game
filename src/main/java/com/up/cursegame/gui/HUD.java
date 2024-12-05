package com.up.cursegame.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.up.cursegame.CurseGame;
import com.up.cursegame.discord.DiscordClientManager;
import com.up.cursegame.discord.DiscordPlayer;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.util.DataUtil;
import de.jcm.discordgamesdk.user.DiscordUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class HUD {
	
	private static final ResourceLocation overlayLocation = new ResourceLocation(CurseGame.MOD_ID, "textures/gui/active-overlay.png");
	
	@SubscribeEvent
	public static void onPostRenderOverlay(RenderGameOverlayEvent.Post event) {
		FontRenderer font = Minecraft.getInstance().font;
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			PlayerData data = DataUtil.getData(Minecraft.getInstance().player);
			if (data == null) data = new PlayerData();
			if (Minecraft.getInstance().player.isAlive()) {
				String livesText = "x" + data.getLives();
				int width = font.width(livesText);
				font.drawShadow(event.getMatrixStack(), livesText, (event.getWindow().getGuiScaledWidth() - width) / 2 - 2, event.getWindow().getGuiScaledHeight() - 39, 0x00FFFFFF);
//				if (lives.getLives() > 0) {
//					String livesText = "x" + lives.getLives();
//					int width = font.width(livesText);
//					font.drawShadow(event.getMatrixStack(), livesText, (event.getWindow().getGuiScaledWidth() - width) / 2 - 2, event.getWindow().getGuiScaledHeight() - 39, 0x00FFFFFF);
//				} else {
//					String deadText = "You died!";
//					int width = font.width(deadText);
//					font.drawShadow(event.getMatrixStack(), deadText, (event.getWindow().getGuiScaledWidth() - width) / 2, event.getWindow().getGuiScaledHeight() - 39, 0x00FFFFFF);
//				}
			}
			if (CurseGame.clientDiscord.isReady()) {
				IFormattableTextComponent discordText = new StringTextComponent("");
				discordText.append(new StringTextComponent(CurseGame.clientDiscord.getCore().isOpen() ? "Discord Active" : "Discord Inactive").withStyle(CurseGame.clientDiscord.getCore().isOpen() ? TextFormatting.GREEN : TextFormatting.RED));
				discordText.append(new StringTextComponent(" - "));
				discordText.append(new StringTextComponent(CurseGame.clientDiscord.getLobby() == null ? "No lobby" : "In lobby " + CurseGame.clientDiscord.getLobby().getId()));
				int width = font.width(discordText);
				font.drawShadow(event.getMatrixStack(), discordText, (event.getWindow().getGuiScaledWidth() - width) / 2 - 2, event.getWindow().getGuiScaledHeight() - 100, 0x00FFFFFF);
				int y = 0;
				try {
				for (DiscordUser user : CurseGame.clientDiscord.getCore().lobbyManager().getMemberUsers(CurseGame.clientDiscord.getLobby())) {
					DiscordPlayer player = CurseGame.clientDiscord.getPlayerManager().getPlayer(user.getUserId());
					IFormattableTextComponent userText = new StringTextComponent("");
					if (player != null) {
						if (player.getUuid().equals(Minecraft.getInstance().player.getUUID())) {
							userText.append(new StringTextComponent(CurseGame.clientDiscord.getCore().voiceManager().isSelfMute() ? "\u23fa" : "\u23fc").withStyle(CurseGame.clientDiscord.getCore().voiceManager().isSelfMute() ? TextFormatting.DARK_RED : TextFormatting.DARK_GREEN));
							userText.append(new StringTextComponent(CurseGame.clientDiscord.getCore().voiceManager().isSelfDeaf()? "\u2205" : "").withStyle(CurseGame.clientDiscord.getCore().voiceManager().isSelfDeaf() ? TextFormatting.DARK_RED : TextFormatting.DARK_GREEN));
						} else {
							userText.append(new StringTextComponent(player.getDistance() > 75 ? "\u23fa" : "\u23fc").withStyle(getDistanceColor(player)));
						}
					}
					userText.append(new StringTextComponent(" "));
					userText.append(new StringTextComponent(player != null ? player.getName() : "(Not in game)"));
					userText.append(new StringTextComponent(" (" + user.getUsername() + ")"));
					userText.append(new StringTextComponent(" " + player.getDistance() + "m"));
					font.drawShadow(event.getMatrixStack(), userText, 10, 100 + y, 0x00FFFFFF);
					y += 10;
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (data.isActive()) {
				Minecraft.getInstance().getTextureManager().bind(overlayLocation);
				RenderSystem.enableBlend();
				GlStateManager._scaled(event.getWindow().getWidth() / 2048d, event.getWindow().getHeight() / 2048d, 1);
				AbstractGui.blit(event.getMatrixStack(), 0, 0, 0, 0f, 0f, 1024, 1024, 1024, 1024);
				GlStateManager._scaled(2048d / event.getWindow().getWidth(), 2048d / event.getWindow().getHeight(), 1);
				RenderSystem.defaultBlendFunc();
			}
		}
	}
	
	private static TextFormatting getDistanceColor(DiscordPlayer player) {
		if (player.getDistance() > DiscordClientManager.MAX_VOLUME_RANGE) return TextFormatting.BLACK;
		if (player.getDistance() > (DiscordClientManager.MAX_VOLUME_RANGE - DiscordClientManager.MIN_VOLUME_RANGE) / 2d) return TextFormatting.DARK_GRAY;
		if (player.getDistance() > DiscordClientManager.MIN_VOLUME_RANGE) return TextFormatting.GRAY;
		return TextFormatting.WHITE;
	}
	
}
