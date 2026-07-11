package it.unibo.controller.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerLobby {
    private final int requiredPlayers;
    private final List<String> joinedPlayers = new CopyOnWriteArrayList<>();
    private volatile boolean started = false;

    public PlayerLobby(int requiredPlayers) {
        this.requiredPlayers = requiredPlayers;
    }

    public boolean join(String playerName) {
        if (started) {
            return false;
        }
        joinedPlayers.add(playerName);
        if (joinedPlayers.size() == requiredPlayers) {
            started = true;
            return true;
        }
        return false;
    }

    public boolean lobbyIsPlaying() {
        return started;
    }

    public int joinedCount() {
        return joinedPlayers.size();
    }

    public int requiredPlayers() {
        return requiredPlayers;
    }

    public List<String> joinedPlayers() {
        return List.copyOf(joinedPlayers);
    }
}
