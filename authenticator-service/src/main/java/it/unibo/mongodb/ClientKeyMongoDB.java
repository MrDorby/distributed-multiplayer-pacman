package it.unibo.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Mapping to a collection to store a map username-public key of the user.
 */
@Document(collection = "keymap")
public class ClientKeyMongoDB {
    
    @Id
    private String id;
    private String username;
    private String clientKey;
    
    public ClientKeyMongoDB() {
    
    }
    
    public ClientKeyMongoDB(String username, String clientKey) {
        this.username = username;
        this.clientKey = clientKey;
    }

    /**
     * @return the identifier of the document.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the identifier of the document.
     * @param id the identifier.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the username of the client.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the client.
     * @param username the identifier of the player.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the public key of the client.
     */
    public String getClientKey() {
        return clientKey;
    }

    /**
     * Sets the public key for the client.
     * @param clientKey the public key in a String format.
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

}
