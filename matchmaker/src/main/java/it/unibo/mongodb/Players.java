package it.unibo.mongodb;

/**
 * 
 * Players
 * @param username
 * @param socket
 */
public record Players(
    String username,
    Socket socket
) {
    
}
