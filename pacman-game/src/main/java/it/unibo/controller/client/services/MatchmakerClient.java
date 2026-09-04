package it.unibo.controller.client.services;

import it.unibo.controller.client.common.ConnectionParameters;

import java.util.Optional;

public interface MatchmakerClient {

    /**
     * Joins the matchmaking queue for the specified map.
     *
     * @param mapName   the name of the selected map
     * @param userToken the authenticated user's token
     * @return true if successfully queued or matched, false otherwise
     */
    boolean queue(String mapName, String userToken) throws Exception;

    /**
     * Cancels the active queue session using the stored lobby ID.
     *
     * @param userToken the authenticated user's token
     * @return true if the cancellation was accepted, false otherwise
     */
    boolean cancelQueue(String userToken) throws Exception;

    /**
     * Polls the matchmaker service to check if a match has been assigned.
     *
     * @param userToken the authenticated user's token
     * @return true if the match is ready, false otherwise
     */
    boolean checkQueueStatus(String userToken) throws Exception;

    /**
     * Fetches game server connection credentials using a known match ID.
     *
     * @param matchId   the identifier of the active match
     * @param userToken the authenticated user's token
     * @return {@link ConnectionParameters} containing the host address and ports
     */
    Optional<ConnectionParameters> getServerParametersByMatchId(String matchId, String userToken) throws Exception;

    /**
     * Fetches game server connection credentials directly using the user's token.
     * @param userToken the authenticated user's token.
     * @return Optional containing parameters if match exists, or Optional.empty() if no active match is found.
     */
    Optional<ConnectionParameters> getServerParametersByToken(String userToken) throws Exception;

    /**
     * Leaves an in-progress match using the current match state.
     *
     * @param userToken the authenticated user's token
     * @return true if the request was accepted, false otherwise
     */
    boolean quitMatch(String userToken) throws Exception;

    /**
     * Retrieves the current lobby ID.
     *
     * @return the active lobby ID, or null if no lobby is active or a match was found
     */
    String getCurrentLobbyId();

    /**
     * Retrieves the current match ID.
     *
     * @return the match ID if a match has been found, or {@code null} if still waiting or cleared
     */
    String getCurrentMatchId();

    /**
     * Clears all internally tracked session state.
     */
    void clearMatchmakingData();
}