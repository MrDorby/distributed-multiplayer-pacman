package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.unibo.mongodb.AuthMongoDB;
import it.unibo.mongodb.AuthRepository;

/* Needed for the authentication of the user.*/
@Service
public class AuthDetailsService {

    @Autowired
    private AuthRepository authRepository;

    public AuthMongoDB authenticate(String username, String password, PasswordEncoder passwordEncoder) {
        AuthMongoDB authUser = (AuthMongoDB) loadUserByUsername(username);
        if (authUser.getUsername().equals(username) && passwordEncoder.matches(password, authUser.getPassword())) {
            return authUser;
        }
        throw new UsernameNotFoundException("User not found.");
    }

    public AuthMongoDB loadUserByUsername(String username) throws UsernameNotFoundException {
        return authRepository.findByUsername(username).orElse(null);
            //.orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public AuthMongoDB register(AuthMongoDB authMongoDB) {
        return authRepository.save(authMongoDB);
    }

    public void addKey(String username, String key) {
        authRepository.findByUsername(username).ifPresent(x -> {
            x.setKey(key); 
            authRepository.save(x);
        });
    }
    
}
