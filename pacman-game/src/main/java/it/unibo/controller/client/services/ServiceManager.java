package it.unibo.controller.client.services;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.ConnectionParameters;

/**
 * Manager for the services that the client needs to connect with.
 */
public interface ServiceManager {
    
    /**
     * Obtain the username of the client once it has logged in.
     * @return the String for the username.
     */
    String getUsername();

    /**
     * Obtain the token of the client once it has logged in.
     * @return the String for the token.
     */
    String getToken();

    /**
     * Starts the procedure to login in the user.
     * @param username of the user.
     * @param password of the user for that username.
     * @throws Exception
     */
    void login(String username, String password) throws Exception;

    /**
     * Starts the procedure to register the user in the database.
     * @param username of the new user.
     * @param password of the new user for that username.
     * @return a String containing the return's message.
     * @throws Exception
     */
    String register(String username, String password) throws Exception;

    /**
     * Gets the statistics for the player stored in the long-term databases.
     * @return The Stats relative to the player.
     * @throws Exception
     */
    PlayerStats getPlayerInfo() throws Exception;
    
    // TODO: add for matchmaker and queries.

    /**
     * Enters the player into the matchmaking queue for a specific map.
     *
     * @param mapName the name of the map to queue for.
     * @return true if successfully added to the queue, false otherwise.
     */
    boolean queue(String mapName) throws Exception;

    /**
     * Requests cancellation of the active matchmaking queue session.
     *
     * @return true if the cancellation request succeeded, false otherwise.
     */
    boolean cancelQueue() throws Exception;

    /**
     * Polls the server to check if a match has been found for the player.
     *
     * @return true if a match is ready; false if still searching.
     */
    boolean checkQueueStatus() throws Exception;

    /**
     * Retrieves the network parameters required to connect to the game server for a match.
     *
     * @return the {@link ConnectionParameters} for the game server connection.
     */
    ConnectionParameters getGameServerParameters() throws Exception;

    /**
     * Gets the unique identifier for the player's queue lobby.
     *
     * @return the lobby ID String, or null if not currently in a lobby.
     */
    String getCurrentLobbyId();

    /**
     * Gets the unique identifier for the match found during matchmaking.
     *
     * @return the match ID String, or null if no match has been assigned yet.
     */
    String getCurrentMatchId();

    /**
     * Clears all matchmaking data.
     */
    void clearMatchmakingData();
}
