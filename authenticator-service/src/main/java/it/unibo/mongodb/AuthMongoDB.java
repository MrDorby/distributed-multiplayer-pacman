package it.unibo.mongodb;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;

/**
 * Mapping to a collection to store user infos.
 */
public class AuthMongoDB {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMongoDB.class);
    
    @Id
    private String username;
    //@Indexed(unique = true)
    //private String id;
    private String password;
    private String token;

    public AuthMongoDB(String username, String password, String token) {
        this.password = password;
        this.username = username;
        this.token = token;
    }

    // public AuthMongoDB(String username, String password) {
    //     this.password = password;
    //     this.username = username;
    //     this.token = Optional.empty();
    // }

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

    public String getToken() {
        return this.token;//.orElse("");
    }

    public void setToken(String token) {
        this.token = token;
    }
}
