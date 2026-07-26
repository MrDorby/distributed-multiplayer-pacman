package it.unibo.controller.client.services;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.ConnectionParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * In-memory mock implementation of {@link ServiceManager} used for testing and offline development.
 * <p>
 * Simulates authentication, registration, and stats retrieval with artificial network delays
 * and session state tracking without requiring a live backend server.
 */
public class DummyServiceManager implements ServiceManager {
    private final Map<String, String> userDatabase = new HashMap<>();

    private String currentUsername;
    private String currentToken;

    private volatile String currentLobbyId;
    private volatile String currentMatchId;

    public DummyServiceManager() {
        userDatabase.put("user", "123");
    }

    @Override
    public String getUsername() {
        return currentUsername;
    }

    @Override
    public String getToken() {
        return currentToken;
    }

    @Override
    public void login(String username, String password) throws Exception {
        Thread.sleep(300);
        if (!userDatabase.containsKey(username)) {
            throw new Exception("User does not exist");
        }
        if (!userDatabase.get(username).equals(password)) {
            throw new Exception("Invalid credentials");
        }
        this.currentUsername = username;
        this.currentToken = "dummy_token_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public String register(String username, String password) throws Exception {
        Thread.sleep(300);
        if (userDatabase.containsKey(username)) {
            throw new Exception("Username is already taken");
        }
        userDatabase.put(username, password);
        return "Successfully registered";
    }

    @Override
    public PlayerStats getPlayerInfo() throws Exception {
        if (currentUsername == null) {
            throw new Exception("Not authenticated");
        }
        Thread.sleep(1000);
        return new PlayerStats(currentUsername, 12, 5, 0.1f, 1250);
    }

    @Override
    public boolean queue(String mapName) throws Exception {
        clearMatchmakingData();
        Thread.sleep(1000);
        if (Math.random() < 0.3) {
            return false;
        }
        this.currentLobbyId = "lobby_" + UUID.randomUUID().toString().substring(0, 8);
        return true;
    }

    @Override
    public boolean cancelQueue() {
        if (this.currentLobbyId == null) {
            return false;
        }
        clearMatchmakingData();
        return true;
    }

    @Override
    public boolean checkQueueStatus() throws Exception {
        if (this.currentLobbyId == null) {
            throw new IllegalStateException("Not enqueued");
        }
        Thread.sleep(2000);
        boolean matchFound = Math.random() > 0.3;
        if (matchFound) {
            this.currentMatchId = "match_" + UUID.randomUUID().toString().substring(0, 8);
            return true;
        }
        return false;
    }

    @Override
    public ConnectionParameters getGameServerParameters() {
        return new ConnectionParameters("127.0.0.1", 7777, 7777);
    }

    @Override
    public String getCurrentLobbyId() {
        return this.currentLobbyId;
    }

    @Override
    public String getCurrentMatchId() {
        return this.currentMatchId;
    }

    @Override
    public void clearMatchmakingData() {
        this.currentLobbyId = null;
        this.currentMatchId = null;
    }
}