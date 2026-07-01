package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.unibo.mongodb.AuthMongoDB;
import it.unibo.mongodb.AuthRepository;

/* Needed for the authentication of the user.*/
@Service
public class AuthDetailsService {

    @Autowired
    private AuthRepository authRepository;

    // @Autowired
    // private AuthenticationManager authenticationManager;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    // TODO: this method needs to be revisionated, no more authentication manager.
    public AuthMongoDB authenticate(String username, String password) {
        AuthMongoDB authUser = (AuthMongoDB) loadUserByUsername(username);
        if (authUser.getUsername() == username && authUser.getPassword() == password) {
            return authUser;
        }
        throw new UsernameNotFoundException("User not found.");
    }

    public AuthMongoDB loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findByUsername(username).orElse(null);
            //.orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public boolean register(AuthMongoDB authMongoDB) {
        return authRepository.save(authMongoDB) == authMongoDB ? true : false;
    }

    public void addToken(String username, String token) {
        authRepository.updateToken(username, token);
    }
    
}
