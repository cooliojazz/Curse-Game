package com.up.cursegame.discord;

import com.google.gson.Gson;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbyType;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;

/**
 *
 * @author Ricky
 */
public class DiscordServerManager {
	

	public static final long applicationId = 979195004796960818l;
	private static final String btp1 = "OTc5MTk1MDA0Nzk2OTYwODE4." + "GBm7Pu.";
	private static final String btp2 = "3oq7VDk0PcYapH3DWxI4JIpbDTxmS6k5-" + "qTmyQ";
	private static final String botToken = btp1 + btp2;
	public static final String metadataKey = "curse-game-id";
	private static final String discordApi = "https://discord.com/api/";
	
	private UUID gameId = UUID.randomUUID();
	private Lobby lobby;
	private boolean ready;
	private DiscordPlayerManager playerManager = new DiscordPlayerManager();
//	private HttpClient client = HttpClient.newBuilder()
////				.version(Version.HTTP_1_1)
////				.followRedirects(Redirect.NORMAL)
////				.connectTimeout(Duration.ofSeconds(20))
////				.proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
////				.authenticator(Authenticator.getDefault())
//				.build();
	
	public DiscordServerManager() { }

	public UUID getGameId() {
		return gameId;
	}
	
	public long getLobbyId() {
		return lobby.getId();
	}

	public String getSecret() {
		return lobby.getSecret();
	}

	public DiscordPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	private void deleteLobby() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
			 .uri(URI.create(discordApi + "lobbies/" + lobby.getId()))
			 .header("Authorization", "Bot " + botToken)
			 .DELETE()
			 .build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println("Lobby delete response: " + response.body());
	}
	
	private void createLobby() throws IOException, InterruptedException {
		Gson gson = new Gson();
		HttpRequest request = HttpRequest.newBuilder()
			 .uri(URI.create(discordApi + "lobbies"))
			 .header("Authorization", "Bot " + botToken)
			 .header("Content-Type", "application/json")
			 .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new CreateLobbyRequest(applicationId + "", LobbyType.PRIVATE.ordinal() + 1, Collections.singletonMap(metadataKey, gameId.toString()), 10, null))))
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
