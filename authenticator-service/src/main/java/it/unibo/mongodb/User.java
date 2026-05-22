package it.unibo.mongodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;
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
@Document("Users")
public class User implements UserDetails {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);
    
    @Id
    private String id;
    private String email;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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
        return this.email;
    }

}
