package com.up.cursegame.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.up.cursegame.CurseGameMod;
import com.up.cursegame.discord.DiscordClientManager;
import com.up.cursegame.discord.DiscordPlayer;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.util.PlayerDataUtil;
import de.jcm.discordgamesdk.user.DiscordUser;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Ricky
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class HUD {
	
	private static final ResourceLocation discordLocation = new ResourceLocation(CurseGameMod.MOD_ID, "textures/gui/discord.png");
	public static double activePercent = 0;
	
	@SubscribeEvent
	public static void onPostRenderOverlay(RenderGameOverlayEvent.Post event) {
		MainWindow window = event.getWindow();
		FontRenderer font = Minecraft.getInstance().font;
		if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			PlayerData data = PlayerDataUtil.getData(Minecraft.getInstance().player);
			if (data == null) data = new PlayerData();
			if (Minecraft.getInstance().player.isAlive() && PlayerDataUtil.isAlive(Minecraft.getInstance().player)) {
				String livesText = "x" + data.getLives();
				int width = font.width(livesText);
				font.drawShadow(event.getMatrixStack(), livesText, (event.getWindow().getGuiScaledWidth() - width) / 2 - 1, event.getWindow().getGuiScaledHeight() - 38, 0x00FFFFFF);
			}
			if (CurseGameMod.clientDiscord.isReady()) {
//				discordPlayerList(font, event.getMatrixStack(), window);
				Minecraft.getInstance().getTextureManager().bind(discordLocation);
				AbstractGui.blit(event.getMatrixStack(), 10, event.getWindow().getGuiScaledHeight() - 10 - 7, 12, 8, 0f, 0f, 16, 12, 16, 16);
				IFormattableTextComponent text = new StringTextComponent("");
				text.append(CurseGameMod.clientDiscord.getUser().getUsername());
				font.drawShadow(event.getMatrixStack(), text, 10 + 15, event.getWindow().getGuiScaledHeight() - 10 - 7, 0x00FFFFFF);
				font.drawShadow(event.getMatrixStack(), new StringTextComponent(CurseGameMod.clientDiscord.getCore().voiceManager().isSelfDeaf() ? "\u2205" : "\u23fc").withStyle(CurseGameMod.clientDiscord.getCore().voiceManager().isSelfDeaf() ? TextFormatting.DARK_RED : TextFormatting.DARK_GREEN), 10 + 15 + font.width(text) + 2, event.getWindow().getGuiScaledHeight() - 10 - 7, 0x00FFFFFF);
				if (CurseGameMod.clientDiscord.getCore().voiceManager().isSelfMute()) font.drawShadow(event.getMatrixStack(), new StringTextComponent("\u23fa").withStyle(TextFormatting.DARK_RED), 10 + 15 + font.width(text) + 2 + 8 + 2, event.getWindow().getGuiScaledHeight() - 10 - 7, 0x00FFFFFF);
			}
			if (data.isActive()) {
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.disableAlphaTest();
				RenderSystem.defaultBlendFunc();
				RenderSystem.shadeModel(7425);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuilder();
				bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
				
				int a = (int)(256 * (Math.exp(activePercent * 2) - 1) / 7);
//				int a = (int)(256 * activePercent);
				int b = 0x88;
				int g = 0;
				int r = 0xCC;
				Matrix4f matrix = event.getMatrixStack().last().pose();
				float width = window.getGuiScaledWidth();
				float height = window.getGuiScaledHeight();
				float width2 = width / 2f;
				float height2 = height / 2f;
				
				// Gradient seems to render better with 8 points instead of 4 for some reason
				bufferbuilder.vertex(matrix, width2, height2, 0f).color(0, 0, 0, a / 2).endVertex();
				bufferbuilder.vertex(matrix, 0f,     0f,      0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, 0f,     height2, 0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, 0f,     height,  0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, width2, height,  0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, width,  height,  0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, width,  height2, 0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, width,  0f,      0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, width2, 0f,      0f).color(r, g, b, a).endVertex();
				bufferbuilder.vertex(matrix, 0f,     0f,      0f).color(r, g, b, a).endVertex();

				tessellator.end();
				RenderSystem.shadeModel(7424);
				RenderSystem.disableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
			}
		}
	}
	
	private static void discordPlayerList(FontRenderer font, MatrixStack stack, MainWindow window) {
//		IFormattableTextComponent discordText = new StringTextComponent("");
//		discordText.append(new StringTextComponent(CurseGameMod.clientDiscord.getCore().isOpen() ? "Discord Active" : "Discord Inactive").withStyle(CurseGameMod.clientDiscord.getCore().isOpen() ? TextFormatting.GREEN : TextFormatting.RED));
//		discordText.append(new StringTextComponent(" - "));
//		discordText.append(new StringTextComponent(CurseGameMod.clientDiscord.getLobby() == null ? "No lobby" : "In lobby " + CurseGameMod.clientDiscord.getLobby().getId()));
//		int width = font.width(discordText);
//		font.drawShadow(stack, discordText, (window.getGuiScaledWidth() - width) / 2 - 2, window.getGuiScaledHeight() - 100, 0x00FFFFFF);
		int y = 0;
//		try {
		for (DiscordUser user : CurseGameMod.clientDiscord.getCore().lobbyManager().getMemberUsers(CurseGameMod.clientDiscord.getLobby())) {
			DiscordPlayer player = CurseGameMod.clientDiscord.getPlayerManager().getPlayer(user.getUserId());
			if (player == null || !player.getUuid().equals(Minecraft.getInstance().player.getUUID())) {
				IFormattableTextComponent userText = new StringTextComponent("");
//				if (player != null) {
//					userText.append(new StringTextComponent(player.getDistance() > 75 ? "\u23fa" : "\u23fc").withStyle(getDistanceColor(player)));
//				}
//				userText.append(new StringTextComponent(" "));
				userText.append(new StringTextComponent(player != null ? player.getName() : "(Not in game)"));
				userText.append(new StringTextComponent(" (" + user.getUsername() + ")"));
	//					userText.append(new StringTextComponent(" " + player.getDistance() + "m"));
				font.drawShadow(stack, userText, 10, 100 + y, 0x00FFFFFF);
				y += 10;
			}
		}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	private static TextFormatting getDistanceColor(DiscordPlayer player) {
		if (player.getDistance() > DiscordClientManager.MAX_VOLUME_RANGE) return TextFormatting.BLACK;
		if (player.getDistance() > (DiscordClientManager.MAX_VOLUME_RANGE - DiscordClientManager.MIN_VOLUME_RANGE) / 2d) return TextFormatting.DARK_GRAY;
		if (player.getDistance() > DiscordClientManager.MIN_VOLUME_RANGE) return TextFormatting.GRAY;
		return TextFormatting.WHITE;
	}
	
}
