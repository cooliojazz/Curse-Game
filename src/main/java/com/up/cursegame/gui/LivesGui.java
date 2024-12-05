package com.up.cursegame.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.up.cursegame.CurseGameMod;
import com.up.cursegame.capability.PlayerData;
import com.up.cursegame.capability.PlayerDataCapabilityProvider;
import com.up.cursegame.discord.DiscordPlayer;
import com.up.cursegame.network.PacketHandlers;
import com.up.cursegame.network.PlayerTradeLivesPacket;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

/**
 *
 * @author Ricky
 */
public class LivesGui extends Screen {
	
	private final Button tradeButton = new Button(10, 35, 55, 20, new StringTextComponent("Trade"), tradeLives());
	private final HashMap<DiscordPlayer, Integer> trades = new HashMap<>();
	
	public LivesGui() {
		super(new StringTextComponent("Lives"));
		
	}

	@Override
	public void init(Minecraft mc, int p_231158_2_, int p_231158_3_) {
		super.init(mc, p_231158_2_, p_231158_3_);
		
		addButton(tradeButton);
		
		int y = 60;
		for (DiscordPlayer player : CurseGameMod.clientDiscord.getPlayerManager().getPlayers()) {
			trades.put(player, 0);
			addButton(new Button(10, y, 20, 20, new StringTextComponent("-"), changeTrade(player, -1)));
			addButton(new Button(35, y, 20, 20, new StringTextComponent("+"), changeTrade(player, 1)));
			y += 25;
		}
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		PlayerData data = Minecraft.getInstance().player.getCapability(PlayerDataCapabilityProvider.PLAYER_DATA_CAPABILITY).orElse(null);
		int totalLives = trades.values().stream().collect(Collectors.summingInt(i -> i));
		tradeButton.active = totalLives > 0 && totalLives < data.getLives();
		
		renderBackground(matrix, 0x00444444);
		
		String level = "Lives: " + (data.getLives() - totalLives);
		font.drawShadow(matrix, level, 10, 11, 0x00FFFFFF);
		
		if (data.isActive()) {
			String points = "Lives Post-Deactivation: " + data.getPrevLives();
			font.drawShadow(matrix, points, 10, 21, 0x00FFFFFF);
		}
		
		int y = 66;
		for (DiscordPlayer player : CurseGameMod.clientDiscord.getPlayerManager().getPlayers()) {
			font.drawShadow(matrix, trades.get(player) + " Lives \u2192 " + player.getName(), 60, y, 0x00FFFFFF);
			y += 25;
		}
		
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
	
	private Button.IPressable changeTrade(DiscordPlayer player, int change) {
		return e -> {
				int newTrade = trades.get(player) + change;
				if (newTrade < 0) newTrade = 0;
				trades.put(player, newTrade);
			};
	}
	
	private Button.IPressable tradeLives() {
		return e -> {
				trades.entrySet().forEach(t -> PacketHandlers.send(new PlayerTradeLivesPacket(t.getValue(), t.getKey().getUuid())));
				Minecraft.getInstance().setScreen(null);
			};
	}
}
