package com.up.cursegame.discord;

import com.google.gson.Gson;
import de.jcm.discordgamesdk.NetworkManager;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbyType;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Turns out Discord removed the lobbies feature from their Game SDK, so all of this is useless now and would need to be replaced with some other voice solution
 * @author Ricky
 */
public class DiscordServerManager {
	
	private UUID gameId = UUID.randomUUID();
	private Lobby lobby;
	private boolean ready;
	private DiscordPlayerManager playerManager = new DiscordPlayerManager();
	private HttpClient client = HttpClient.newBuilder().build();
	
	public DiscordServerManager() {
	}

	public UUID getGameId() {
		return gameId;
	}
	
	public long getLobbyId() {
		return lobby == null ? -1 : lobby.getId();
	}

	public String getSecret() {
		return lobby.getSecret();
	}

	public DiscordPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	private void deleteLobby() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			 .uri(URI.create(DiscordInfo.DISCORD_API_URL + "lobbies/" + lobby.getId()))
			 .header("Authorization", "Bot " + DiscordInfo.BOT_TOKEN)
			 .header("User-Agent", "DiscordBot (http://unphan.co, 1.3.0)")
			 .DELETE()
			 .build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println("Lobby delete response: " + response.body());
	}
	
	private void createLobby() throws IOException, InterruptedException {
		Gson gson = new Gson();
		HttpRequest request = HttpRequest.newBuilder()
			 .uri(URI.create(DiscordInfo.DISCORD_API_URL + "lobbies"))
			 .header("Authorization", "Bot " + DiscordInfo.BOT_TOKEN)
			 .header("User-Agent", "DiscordBot (http://unphan.co, 1.3.0)")
			 .header("Content-Type", "application/json")
			 .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new CreateLobbyRequest(DiscordInfo.APPLICATION_ID + "", LobbyType.PRIVATE.ordinal() + 1, Collections.singletonMap(DiscordInfo.METADATA_KEY, gameId.toString()), 10, null))))
			 .build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) throw new RuntimeException("Error creating discord lobby!\n" + response.body());
		lobby = gson.fromJson(response.body(), Lobby.class);
		System.out.println("Created lobby " + lobby.getId() + " for game " + gameId);
	}
	
	public void enable() {
		try {
			createLobby();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ready = true;
	}

	public void disable() {
		playerManager = new DiscordPlayerManager();
		try {
			deleteLobby();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ready = false;
	}

	public boolean isReady() {
		return ready;
	}
	
	private static class CreateLobbyRequest {
		
		String application_id;
		int type;
		Map<String, String> metadata;
		int capacity;
		String region;

		public CreateLobbyRequest(String application_id, int type, Map<String, String> metadata, int capacity, String region) {
			this.application_id = application_id;
			this.type = type;
			this.metadata = metadata;
			this.capacity = capacity;
			this.region = region;
		}
	}
	
}
