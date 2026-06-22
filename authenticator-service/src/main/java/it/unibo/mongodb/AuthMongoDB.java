package it.unibo.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Mapping to a collection to store user infos.
 */
@Document("auth")
public class AuthMongoDB implements UserDetails {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMongoDB.class);
    
    @Id
    private String username;
    //@Indexed(unique = true)
    //private String id;
    private String password;
    private Optional<String> token;

    public AuthMongoDB(String username, String password, String token) {
        this.password = password;
        this.username = username;
        this.token = Optional.of(token);
    }

    public AuthMongoDB(String username, String password) {
        this.password = password;
        this.username = username;
        this.token = Optional.empty();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authority = new ArrayList<SimpleGrantedAuthority>();
        authority.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authority;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return this.token.orElse("");
    }

    public void setToken(String token) {
        this.token = Optional.of(token);
    }
}
