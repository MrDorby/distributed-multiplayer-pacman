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
public class AuthDetailsService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    //@Autowired
    //private PasswordEncoder passwordEncoder;

    public Authentication authenticate(UsernamePasswordAuthenticationToken credentials) {
        return this.authenticationManager.authenticate(credentials);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (AuthMongoDB) authRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    public boolean register(AuthMongoDB authMongoDB) {
        return authRepository.save(authMongoDB) == authMongoDB ? true : false;
    }

    public void addToken(String username, String token) {
        authRepository.updateToken(username, token);
    }
    
}
