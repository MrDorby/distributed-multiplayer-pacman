package it.unibo.mongodb;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Mapping to a collection to store user infos.
 */
@Document(collection = "auth")
public class AuthMongoDB {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMongoDB.class);
    
    //@Indexed(unique = true)
    @Id
    private String id;
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

    // public AuthMongoDB(String username, String password) {
    //     this.password = password;
    //     this.username = username;
    //     this.token = Optional.empty();
    // }
    
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
        return this.key;//.orElse("");
    }

    public void setKey(String key) {
        this.key = key;
    }
}
