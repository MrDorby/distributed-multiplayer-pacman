package it.unibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.unibo.mongodb.AuthMongoDB;
import it.unibo.mongodb.AuthRepository;
import it.unibo.mongodb.ClientKeyMongoDB;
import it.unibo.mongodb.ClientKeyRepository;

/* Needed for the authentication of the user.*/
@Service
public class AuthDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private ClientKeyRepository clientKeyRepository;

    /**
     * Authenticates the user by providing the username and the its password. 
     * @param username the identifier of the player
     * @param password the password inserted.
     * @param passwordEncoder the encoder used by the authenticator.
     * @return the user authenticated.
     */
    public AuthMongoDB authenticate(String username, String password, PasswordEncoder passwordEncoder) {
        AuthMongoDB authUser = (AuthMongoDB) loadUserByUsername(username);
        if (authUser.getUsername().equals(username) && passwordEncoder.matches(password, authUser.getPassword())) {
            return authUser;
        }
        throw new UsernameNotFoundException("User not found.");
    }

    /**
     * Loads the user of AuthMongoRepository by using its username.
     * @param username the identifier of the player.
     * @return the AuthMongoDB user info.
     */
    public AuthMongoDB loadUserByUsername(String username) {
        return authRepository.findByUsername(username).orElse(null);
            //.orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    /**
     * Registers the new user to the AuthMongoRepository.
     * @param authMongoDB the new user.
     * @return the new user inserted.
     */
    public AuthMongoDB register(AuthMongoDB authMongoDB) {
        return authRepository.save(authMongoDB);
    }

    /**
     * Sets the public key of the authenticator who let the login for the user.
     * @param username the identifier of the user.
     * @param key the public key of the authenticator instance.
     */
    public void addKey(String username, String key) {
        authRepository.findByUsername(username).ifPresent(x -> {
            x.setKey(key); 
            authRepository.save(x);
        });
    }

    /* ############################################################### */
    /* This part is dedicated to the Client Key Repository where the authenticator */
    /* instances save the "username:publickey" pair of the user. */

    /**
     * Registers the new pair username, public key of the user.
     * @param clientKeyMongoDB the new client.
     * @return the client just created/saved.
     */
    public ClientKeyMongoDB registerClientKey(ClientKeyMongoDB clientKeyMongoDB) {
        return this.clientKeyRepository.save(clientKeyMongoDB);
    }

    /**
     * Finds the specific pair by means of the username.
     * @param username the identifier of the client.
     * @return the requested client key object.
     */
    public ClientKeyMongoDB findClientKeyByUsername(String username) {
        return this.clientKeyRepository.findByUsername(username).orElse(null);
    }
    
}
