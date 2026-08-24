package it.unibo.mongodb;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Mapping to a collection to store user infos.
 */
@Document(collection = "auth")
public class AuthMongoDB {
    
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    private String key;

    public AuthMongoDB() {
        
    }

    public AuthMongoDB(String username, String password, String key) {
        this.password = password;
        this.username = username;
        this.key = key;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
