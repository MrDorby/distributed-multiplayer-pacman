package it.unibo.controller.server;

import java.util.ArrayList;
import java.util.List;

/**
 * A fixed 4-player staging room that manages player registrations before a match starts.
 * <p>
 * While the lobby is in the staging phase players can be freely added or removed as their network connections fluctuate.
 * Once the game begins, the player list becomes immutable.
 * </p>
 */
public class FourManLobby implements PlayerLobby {
    private static final int MAX_LOBBY_SIZE = 4;
    private final int requiredPlayers;
    private final List<String> players = new ArrayList<>();
    private LobbyState state;

    public FourManLobby() {
        this.requiredPlayers = MAX_LOBBY_SIZE;
        this.state = LobbyState.WAITING;
    }

    @Override
    public synchronized void addPlayer(String playerName) {
        if (state != LobbyState.WAITING) {
            return;
        }
        players.add(playerName);
    }

    @Override
    public synchronized void removePlayer(String playerName) {
        if (state != LobbyState.WAITING) {
            return;
        }
        players.remove(playerName);
    }

    @Override
    public synchronized LobbyState getState() {
        return state;
    }

    @Override
    public synchronized void setState(LobbyState state) {
        this.state = state;
    }

    @Override
    public synchronized int getCurrentPlayerCount() {
        return players.size();
    }

    @Override
    public synchronized int getRequiredPlayerCount() {
        return requiredPlayers;
    }

    @Override
    public synchronized boolean isFull() {
        return players.size() == requiredPlayers;
    }

    @Override
    public synchronized List<String> getPlayers() {
        return new ArrayList<>(players);
    }
}
