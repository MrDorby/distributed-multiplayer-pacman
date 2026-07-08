package it.unibo.controller.client.services;

/**
 * 
 * ServiceManager
 */
public interface ServiceManager {
    
    /**
     * 
     * @return
     */
    String getUsername();

    /**
     * 
     * @return
     */
    String getToken();

    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    void login(String username, String password) throws Exception;

    /**
     * 
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    String register(String username, String password) throws Exception;

    // TODO: add for matchmaker and queries.
    
}
