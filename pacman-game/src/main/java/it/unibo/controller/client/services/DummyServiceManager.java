package it.unibo.controller.client.services;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.ConnectionParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory mock implementation of {@link ServiceManager} used for testing and offline development.
 * <p>
 * Simulates authentication, registration, stats retrieval and matchmaking with artificial delays.
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
        Thread.sleep(500);
        if (!userDatabase.containsKey(username)) {
            throw new Exception("User does not exist");
        }
        if (!userDatabase.get(username).equals(password)) {
            throw new Exception("Invalid credentials");
        }
        this.currentUsername = username;
        this.currentToken = "token_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public String register(String username, String password) throws Exception {
        Thread.sleep(500);
        if (userDatabase.containsKey(username)) {
            throw new Exception("Username is already taken");
        }
        userDatabase.put(username, password);
        return "Successfully registered";
    }

    @Override
    public PlayerStats getPlayerInfo() throws Exception {
        Thread.sleep(500);
        return new PlayerStats(currentUsername, 12, 5, 0.1f, 1250);
    }

    @Override
    public boolean queue(String mapName) throws Exception {
        clearMatchmakingData();
        Thread.sleep(500);
        if (Math.random() > 0.3) {
            this.currentLobbyId = "lobby_" + UUID.randomUUID().toString().substring(0, 8);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelQueue() {
        clearMatchmakingData();
        return true;
    }

    @Override
    public boolean checkQueueStatus() throws Exception {
        Thread.sleep(2000);
        if (Math.random() > 0.3) {
            this.currentMatchId = "match_" + UUID.randomUUID().toString().substring(0, 8);
            return true;
        }
        return false;
    }

    @Override
    public Optional<ConnectionParameters> getGameServerParametersByMatchId() throws Exception {
        Thread.sleep(500);
        return Optional.of(new ConnectionParameters("127.0.0.1", 7777, 7777));
    }

    @Override
    public Optional<ConnectionParameters> getGameServerParametersByToken() throws Exception {
        Thread.sleep(500);
        if (Math.random() > 0.5) {
            this.currentMatchId = "match_" + UUID.randomUUID().toString().substring(0, 8);
            return Optional.of(new ConnectionParameters("127.0.0.1", 7777, 7777));
        }
        return Optional.empty();
    }

    @Override
    public boolean quitMatch() throws Exception {
        Thread.sleep(500);
        clearMatchmakingData();
        return true;
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