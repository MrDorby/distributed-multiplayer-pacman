package it.unibo.controller.client.services;

import it.unibo.controller.client.common.PlayerStats;

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
        Thread.sleep(5000);
        return new PlayerStats(currentUsername, 12, 5, 0.1f, 1250);
    }
}