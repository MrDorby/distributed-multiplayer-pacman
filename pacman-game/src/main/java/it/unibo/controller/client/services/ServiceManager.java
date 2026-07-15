package it.unibo.controller.client.services;

import it.unibo.controller.client.common.Stats;

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
    Stats getPlayerInfo() throws Exception;
    
    // TODO: add for matchmaker and queries.
    
}
