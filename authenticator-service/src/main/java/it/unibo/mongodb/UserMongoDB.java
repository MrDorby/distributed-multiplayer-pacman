package it.unibo.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Mapping to a collection to store user infos.
 */
@Document("Users")
public class UserMongoDB implements UserDetails {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMongoDB.class);
    
    @Id
    //@Indexed(unique = true)
    private String id;
    private String email;
    private String password;
    private Optional<String> username;

    public UserMongoDB(String email, String username, String password) {
        this.email = email;
        this.password = password;
        this.username = Optional.of(username);
    }

    public UserMongoDB(String email, String password) {
        this.email = email;
        this.password = password;
        this.username = Optional.empty();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authority = new ArrayList<SimpleGrantedAuthority>();
        authority.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authority;
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }
    @Override
    public String getUsername() {
        return this.username.orElse("");
    }

    public String getEmail() {
        return this.email;
    }

}
