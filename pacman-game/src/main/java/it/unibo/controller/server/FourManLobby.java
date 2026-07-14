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
    private boolean isPlaying;

    public FourManLobby() {
        this.requiredPlayers = MAX_LOBBY_SIZE;
        this.isPlaying = false;
    }

    @Override
    public void addPlayer(String playerName) {
        if (isPlaying) {
            return;
        }
        players.add(playerName);
    }

    @Override
    public void removePlayer(String playerName) {
        if (isPlaying) {
            return;
        }
        players.remove(playerName);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    @Override
    public int getCurrentPlayerCount() {
        return players.size();
    }

    @Override
    public int getRequiredPlayerCount() {
        return requiredPlayers;
    }

    @Override
    public boolean isFull() {
        return players.size() == requiredPlayers;
    }

    @Override
    public List<String> getPlayers() {
        return new ArrayList<>(players);
    }
}
