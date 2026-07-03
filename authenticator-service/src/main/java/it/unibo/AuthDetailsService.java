package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    // @Autowired
    // private AuthenticationManager authenticationManager;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

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
        //authRepository.updateKey(username, key);
        authRepository.findByUsername(username).ifPresent(x -> {
            x.setKey(key); 
            authRepository.save(x);
        });
    }
    
}
