package com.up.cursegame.discord;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.DiscordEventAdapter;
import de.jcm.discordgamesdk.GameSDKException;
import de.jcm.discordgamesdk.Result;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.lobby.LobbySearchQuery;
import de.jcm.discordgamesdk.user.DiscordUser;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Ricky
 */
public class DiscordClientManager {
	
	public static final int MIN_VOLUME_RANGE = 10;
	public static final int MAX_VOLUME_RANGE = 100;

	private DiscordEventAdapter eventHandler = new DiscordEventHandler();
	private boolean runCallbacks = false;
	private Core core;
	private DiscordUser user;
	private Lobby lobby;
	private boolean ready;
	private DiscordPlayerManager playerManager = new DiscordPlayerManager();
	
	public DiscordClientManager() {
		try {
			if (System.getProperties().getProperty("os.name").contains("Windows")) {
				Core.init(getClass().getResource("/discord_game_sdk.dll"));
			} else {
				Core.init(getClass().getResource("/discord_game_sdk.so"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(() -> {
				while (true) {
					try {
						if (runCallbacks) core.runCallbacks();
						Thread.sleep(15);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "Discord Callback").start();
	}

	public Core getCore() {
		return core;
	}

	public DiscordUser getUser() {
		return user;
	}

	public Lobby getLobby() {
		return lobby;
	}

	public DiscordPlayerManager getPlayerManager() {
		return playerManager;
	}
	
	private void findLobby(long lobbyId, String secret) {
		core.lobbyManager().connectLobby(lobbyId, secret, (result, lobby) -> {
				System.out.println("Join lobby: " + result);
				joinLobby(lobby);
			});
	}
	
	private void joinLobby(Lobby lobby) {
		this.lobby = lobby;
		core.lobbyManager().connectVoice(lobby);
		ready = true;
	}
	
	private void leaveLobby(Lobby lobby) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		core.lobbyManager().disconnectLobby(lobby, result -> {
				System.out.println("Leave lobby: " + result);
				if (result == Result.OK) {
					DiscordClientManager.this.lobby = null;
				}
				future.complete(null);
			});
		try {
			future.get();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
	}
	
//	private void createActivity() {
//		Activity testActivity = new Activity();
//		testActivity.setDetails("Test Activity Please Ignore");
//		testActivity.setType(ActivityType.PLAYING);
//		testActivity.setState("Test State");
//		testActivity.setInstance(true);
//		testActivity.party().setID("Test Party");
//		testActivity.party().size().setCurrentSize(1);
//		testActivity.party().size().setMaxSize(2);
//		testActivity.secrets().setJoinSecret("Test Join Secret");
//		core.activityManager().updateActivity(testActivity);
////		new Thread(() -> {
////			core.overlayManager().openActivityInvite(ActivityActionType.JOIN, result -> {
////					System.out.println(result);
////				});
////		}).start();
//	}
	
	public void enable(long lobbyId, String secret) {
		try {
			CreateParams params = new CreateParams();
			params.setClientID(DiscordServerManager.applicationId);
			params.setFlags(CreateParams.getDefaultFlags());
			params.registerEventHandler(eventHandler);
			
			core = new Core(params);
			runCallbacks = true;
			
			int retry = 0;
			while (retry < 5) {
				try {
					user = core.userManager().getCurrentUser();
					System.out.println("Connected to Discord as " + user.getUsername() + " (" + user.getUserId() + ")");
					break;
				} catch (GameSDKException ex) {
					retry++;
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (retry >= 5) System.out.println("Could not get discord user!!");
			

//			LobbySearchQuery query = core.lobbyManager().getSearchQuery();
////			LobbySearchQuery query = core.lobbyManager().getSearchQuery().filter("metadata." + metadataKey, LobbySearchQuery.Comparison.EQUAL, LobbySearchQuery.Cast.STRING, gameId.toString());
//			core.lobbyManager().search(query, result -> {
//					if (result == Result.OK) {
//						core.lobbyManager().getLobbies().forEach(core.lobbyManager()::deleteLobby);
//					} else {
//						throw new RuntimeException("Discord broken!!");
//					}
//				});
			
			findLobby(lobbyId, secret);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disable() {
		playerManager = new DiscordPlayerManager();
		leaveLobby(lobby);
		user = null;
		runCallbacks = false;
		core.close();
		core = null;
		ready = false;
	}

	public boolean isReady() {
		return ready;
	}
	
	private class DiscordEventHandler extends DiscordEventAdapter {

//		@Override
//		public void onCurrentUserUpdate() {
//			super.onCurrentUserUpdate();
//			System.out.println("~~~~~~~~~~~~~~~~~~ USER UPDATE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onMemberUpdate(long lobbyId, long userId) {
//			super.onMemberUpdate(lobbyId, userId);
//			System.out.println("~~~~~~~~~~~~~~~~~~ MEMBER UPDATE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onMessage(long peerId, byte channelId, byte[] data) {
//			super.onMessage(peerId, channelId, data);
//			System.out.println("~~~~~~~~~~~~~~~~~~ ON MESSAGE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onSpeaking(long lobbyId, long userId, boolean speaking) {
//			super.onSpeaking(lobbyId, userId, speaking);
//			System.out.println("~~~~~~~~~~~~~~~~~~ ON SPEAKING! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onNetworkMessage(long lobbyId, long userId, byte channelId, byte[] data) {
//			super.onNetworkMessage(lobbyId, userId, channelId, data);
//			System.out.println("~~~~~~~~~~~~~~~~~~ NETWORK MESSAGE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onMemberConnect(long lobbyId, long userId) {
//			super.onMemberConnect(lobbyId, userId);
//			System.out.println("~~~~~~~~~~~~~~~~~~ MEMBER JOIN! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onMemberDisconnect(long lobbyId, long userId) {
//			super.onMemberDisconnect(lobbyId, userId);
//				System.out.println("~~~~~~~~~~~~~~~~~~ MEMBER LEAVE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onLobbyMessage(long lobbyId, long userId, byte[] data) {
//			super.onLobbyMessage(lobbyId, userId, data);
//			System.out.println("~~~~~~~~~~~~~~~~~~ LOBBY MESSAGE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onLobbyUpdate(long lobbyId) {
//			super.onLobbyUpdate(lobbyId);
//			System.out.println("~~~~~~~~~~~~~~~~~~ LOBBY UPDATE! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onRelationshipRefresh() {
//			super.onRelationshipRefresh();
//			System.out.println("~~~~~~~~~~~~~~~~~~ RELATIONSHIP REFRESH! ~~~~~~~~~~~~~~~~~~~");
//		}
//
//		@Override
//		public void onRelationshipUpdate(Relationship relationship) {
//			super.onRelationshipUpdate(relationship);
//			System.out.println("~~~~~~~~~~~~~~~~~~ RELATIONSHIP UPDATE! ~~~~~~~~~~~~~~~~~~~");
//		}

	};
}
